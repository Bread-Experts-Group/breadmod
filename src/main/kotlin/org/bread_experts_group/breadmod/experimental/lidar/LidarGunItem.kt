package org.bread_experts_group.breadmod.experimental.lidar

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.item.IRenderingItem
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.render.translateToSide
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarHandler
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarSound
import org.bread_experts_group.breadmod.registry.KeyMappings
import org.bread_experts_group.breadmod.registry.item.IKeyboardItem
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.HitResult
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.normalizedHitPos
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.raycast
import org.bread_experts_group.breadmod.util.times
import org.bread_experts_group.breadmod.util.toVec3
import kotlin.math.floor
import kotlin.math.round

class LidarGunItem : Item(Properties().stacksTo(1)), IRenderingItem, IMouseItem, IKeyboardItem {
	private fun Double.reverse(): Double = round((1 - this) * 100) / 100

	/**
	 * @return The raw x, y and z coordinate of the block face before processing.
	 */
	private fun getHitLoc(hit: HitResult<BlockState>): Vec3 {
		val pos = hit.blockPosition
		val loc = hit.hitPosition
		val (nX, nY, nZ) = normalizedHitPos(loc, pos)
		return when (hit.hitSide) {
			DOWN -> Vec3(nX, nZ.reverse(), nY)
			UP -> Vec3(nX, nZ, nY)
			NORTH -> Vec3(nX.reverse(), nY.reverse(), nZ)
			SOUTH -> Vec3(nX, nY.reverse(), nZ)
			WEST -> Vec3(nY.reverse(), nZ, nX)
			EAST -> Vec3(nY.reverse(), nZ.reverse(), nX)
		}
	}

	/**
	 * Not to be confused with LidarBlock#getPixelPos,
	 * that method handles transforming the longs representing the pixels into their relative positions.
	 *
	 * This method simply returns an x & y coordinate for the provided [hit].
	 */
	private fun getPixelPos(hit: HitResult<BlockState>): Vec3 {
		val loc = this.getHitLoc(hit)
		val floorX = floor(loc.x * 16) / 16
		val floorY = floor(loc.y * 16) / 16
		val floorZ = loc.z
		return when (hit.hitSide) {
			DOWN -> Vec3(floorY, floorX + 0.0625, -floorZ)
			UP -> Vec3(-floorY + 1 - 0.0625, -floorX, floorZ - 1)
			NORTH -> Vec3(floorX, -floorY, -floorZ)
			SOUTH -> Vec3(floorX, -floorY, floorZ - 1)
			WEST -> Vec3(floorY, -floorX, -floorZ)
			EAST -> Vec3(floorY, -floorX, floorZ - 1)
		}
	}

	private fun getAbsolutePixelIndex(hit: HitResult<BlockState>): Int {
		val pos = this.getPixelPos(hit).times(16f)
		val x = pos.x.toInt()
		val y = if (pos.y < 0.0) -pos.y.toInt() else pos.y.toInt()
		return Mth.clamp(x, 0, 15) + (Mth.clamp(y, 0, 15) * 16)
	}

	private fun fireLidar(player: Player, deviation: Float, offsetX: Float, offsetY: Float) {
		player.rayCast(100.0, blocks(), deviation, offsetX, offsetY)?.let { hit ->
			val section = LidarHandler.getSection(hit.blockPosition, player.level())
			val block = section.getBlock(hit.blockPosition)
			val absoluteIndex = this.getAbsolutePixelIndex(hit)
			block.setPixelForIndexAndSide(absoluteIndex, hit.hitSide, this.getPixelPos(hit).z, true)
			section.setMeshInvalid()
		}
	}

	private var debug: Boolean = false
	private fun renderLidarTarget(event: RenderLevelStageEvent) {
//		val level = localClient.player?.level() ?: return
		if (event.stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			val poseStack = event.poseStack
			poseStack.pushPose()
			event.camera.raycast(100.0, blocks())?.let { hit ->
//				val block = LidarHandler.getSection(hit.blockPosition, level).getBlock(hit.blockPosition)
				val blockPos = hit.blockPosition.toVec3()
				poseStack.offsetRenderToCameraPos(blockPos, event.camera, false)
				poseStack.translateToSide(hit.hitSide)
				poseStack.translate(this.getPixelPos(hit))
//				val absoluteIndex = this.getAbsolutePixelIndex(hit)
				// divide the 0..255 index of the pixels to 0..3 index of the arrays
//				val arrayIndex = absoluteIndex / 64
//				displayClientMessage(block.formatString(arrayIndex, hit.hitSide))
				poseStack.scaleFlat(1 / 16f)
				drawQuad(poseStack, renderType = ModRenderType.LIDAR)
			}
			poseStack.popPose()
		}
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		val player = entity as? Player ?: return
		if (level.isClientSide && isSelected) {
			if (LidarHandler.burstScanTimeRemaining > 0) {
				repeat(50) {
					this.fireLidar(player, 0.013f, -LidarHandler.burstY, it.toFloat() + 0.5f)
					this.fireLidar(player, 0.013f, -LidarHandler.burstY, -it.toFloat() - 0.5f)
					this.fireLidar(player, 0.013f, LidarHandler.burstY, it.toFloat() + 0.5f)
					this.fireLidar(player, 0.013f, LidarHandler.burstY, -it.toFloat() - 0.5f)

//					this.fireLidar(player, 0.013f, it.toFloat() + 0.5f, -LidarHandler.burstY)
//					this.fireLidar(player, 0.013f, -it.toFloat() - 0.5f, -LidarHandler.burstY)
//					this.fireLidar(player, 0.013f, it.toFloat() + 0.5f, LidarHandler.burstY)
//					this.fireLidar(player, 0.013f, -it.toFloat() - 0.5f, LidarHandler.burstY)
				}
				LidarHandler.burstY -= 0.18f
			}
			LidarHandler.burstScanTimeRemaining -= 1
			if (LidarHandler.burstScanTimeRemaining == 0) {
				LidarHandler.isBurstScanning = false
				LidarHandler.burstY = 0f
			}
		}
	}

	override fun renderLevelStageEvent(
		event: RenderLevelStageEvent,
		bufferSource: MultiBufferSource,
		player: LocalPlayer
	) {
		if (!player.getItemBySlot(EquipmentSlot.HEAD).`is`(ModItems.LIDAR_HELMET)) return
		if (!player.isHolding(this)) return
		if (this.debug) this.renderLidarTarget(event)
		// todo currently tied to framerate, need to add a limiter or use inventoryTick...
		if (localClient.options.keyUse.isDown) this.fireLidar(player, LidarHandler.currentDeviation, 0f, 0f)
	}

	override fun onMouseInputPre(
		mouseEvent: InputEvent.MouseButton.Pre,
		heldStack: ItemStack,
		level: ClientLevel,
		player: LocalPlayer
	) {
		if (!player.getItemBySlot(EquipmentSlot.HEAD).`is`(ModItems.LIDAR_HELMET)) return
		if (localClient.screen != null) return
		if (mouseEvent.button != 1) return
		if (LidarHandler.lidarSound == null) LidarHandler.lidarSound = LidarSound(player.position())
		LidarHandler.onMouseInput(mouseEvent)
	}

	override fun onMouseScroll(
		scrollingEvent: InputEvent.MouseScrollingEvent,
		heldStack: ItemStack,
		level: ClientLevel,
		player: LocalPlayer
	) {
		if (!player.getItemBySlot(EquipmentSlot.HEAD).`is`(ModItems.LIDAR_HELMET)) return
		if (localClient.options.keyUse.isDown) {
			scrollingEvent.isCanceled = true
			LidarHandler.currentDeviation += (scrollingEvent.scrollDeltaY / 100.0).toFloat()
		}
	}

	override fun onKeyboardPress(
		keyEvent: InputEvent.Key,
		heldStack: ItemStack,
		player: Player
	) {
		val level = player.level()
		if (keyEvent.key == KeyMappings.lidarBurstScan.key.value && keyEvent.action == InputConstants.PRESS && !LidarHandler.isBurstScanning) {
			level.playLocalSound(player, ModSounds.LIDAR_BURST.get(), SoundSource.AMBIENT, 1f, 1f)
			LidarHandler.burstScanTimeRemaining = 180
			LidarHandler.isBurstScanning = true
			LidarHandler.burstY = 32f
		}
	}
}