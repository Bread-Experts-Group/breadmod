package org.bread_experts_group.breadmod.experimental.lidar

import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.item.IRenderingItem
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.render.translateToSide
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarHandler
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
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

class LidarGunItem : Item(Properties()), IRenderingItem {
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

	private fun fireLidar(player: Player) {
		player.rayCast(50.0, blocks(), 0.1f)?.let { hit ->
			val section = LidarHandler.getSection(hit.blockPosition, player.level())
			val block = section.getBlock(hit.blockPosition)
			val absoluteIndex = this.getAbsolutePixelIndex(hit)
			// todo zIndex in this method needs to be replaced with a better way to place the dot on the correct z plane of the block
			//  not to mention some blocks have more than one offset per block side so this solution falls apart at that.
			block.setPixelForIndexAndSide(absoluteIndex, hit.hitSide, this.getPixelPos(hit).z, true)
			section.setDynamicRendering()
		}
	}

	private var debug: Boolean = false
	private fun renderLidarTarget(event: RenderLevelStageEvent) {
//		val level = localClient.player?.level() ?: return
		if (event.stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			val poseStack = event.poseStack
			poseStack.pushPose()
			event.camera.raycast(100.0, blocks(), 0.01)?.let { hit ->
//				val block = LidarHandler.getSection(hit.blockPosition, level).getBlock(hit.blockPosition)
				val blockPos = hit.blockPosition.toVec3()
				poseStack.offsetRenderToCameraPos(blockPos, event.camera, false)
				poseStack.translateToSide(hit.hitSide)
				poseStack.translate(this.getPixelPos(hit))
				val absoluteIndex = this.getAbsolutePixelIndex(hit)
				// divide the 0..255 index of the pixels to 0..3 index of the arrays
				val arrayIndex = absoluteIndex / 64
//				displayClientMessage(block.formatString(arrayIndex, hit.hitSide))
				poseStack.scaleFlat(1 / 16f)
				drawQuad(poseStack, renderType = ModRenderType.LIDAR)
			}
			poseStack.popPose()
		}
	}

	override fun renderLevelStageEvent(
		event: RenderLevelStageEvent,
		bufferSource: MultiBufferSource,
		player: LocalPlayer
	) {
		if (this.debug) this.renderLidarTarget(event)
		val level = player.level()
		if (!level.isClientSide) return
		if (!localClient.options.keyUse.isDown) return
		if (!player.isHolding(this)) return
		// todo currently tied to framerate, need to add a limiter or use inventoryTick...
		this.fireLidar(player)
	}
}