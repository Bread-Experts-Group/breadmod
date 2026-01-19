package org.bread_experts_group.breadmod.experimental.lidar

import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.item.IRenderingItem
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.render.translateToSide
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarBlock
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.HitResult
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.displayClientMessage
import org.bread_experts_group.breadmod.util.normalizeHitLoc
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.raycast
import org.bread_experts_group.breadmod.util.times
import org.bread_experts_group.breadmod.util.toVec3
import org.joml.Vector2d
import kotlin.math.floor
import kotlin.math.round

class LidarGunItem : Item(Properties()), IRenderingItem {
	val lidarBlocks: MutableMap<BlockPos, LidarBlock> = mutableMapOf()

	private fun Double.reverse(): Double = round((1 - this) * 100) / 100

	private fun getHitLoc(hit: HitResult<BlockState>): Vector2d {
		val pos = hit.blockPosition
		val loc = hit.hitPosition
		val nX = normalizeHitLoc(loc.x, pos.x)
		val nY = normalizeHitLoc(loc.y, pos.y)
		val nZ = normalizeHitLoc(loc.z, pos.z)
		return when (hit.hitSide) {
			DOWN -> Vector2d(nX, nZ.reverse())
			UP -> Vector2d(nX, nZ)
			NORTH -> Vector2d(nX.reverse(), nY.reverse())
			SOUTH -> Vector2d(nX, nY.reverse())
			WEST -> Vector2d(nY.reverse(), nZ)
			EAST -> Vector2d(nY.reverse(), nZ.reverse())
		}
	}

	private fun getRawPixelPos(hit: HitResult<BlockState>): Vec3 {
		val loc = this.getHitLoc(hit)
		val floorX = floor(loc.x * 16) / 16
		val floorY = floor(loc.y * 16) / 16
		return when (hit.hitSide) {
			DOWN -> Vec3(floorY, floorX + 0.0625, 0.0)
			UP -> Vec3(-floorY + 1 - 0.0625, -floorX, 0.0)
			NORTH -> Vec3(floorX, -floorY, 0.0)
			SOUTH -> Vec3(floorX, -floorY, 0.0)
			WEST -> Vec3(floorY, -floorX, 0.0)
			EAST -> Vec3(floorY, -floorX, 0.0)
		}
	}

	private fun getPixelPosForLongs(side: Direction, absolute: Int): Vec3 {
		val floorX = floor((absolute / 16.0)) / 16
		val floorY = floor((absolute % 16.0)) / 16
		return when (side) {
			DOWN -> Vec3(floorY, floorX, 0.0)
			UP -> Vec3(floorY, -floorX, 0.0)
			NORTH -> Vec3(floorY, -floorX, 0.0)
			SOUTH -> Vec3(floorY, -floorX, 0.0)
			WEST -> Vec3(floorY, -floorX, 0.0)
			EAST -> Vec3(floorY, -floorX, 0.0)
		}
	}

	private fun getPixelIndex(hit: HitResult<BlockState>): Int {
		val pos = this.getRawPixelPos(hit).times(16f)
		val x = pos.x.toInt()
		val y = if (pos.y < 0.0) -pos.y.toInt() else pos.y.toInt()
		return Mth.clamp(x, 0, 15) + (Mth.clamp(y, 0, 15) * 16)
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		if (!level.isClientSide) return
		if (!localClient.options.keyUse.isDown) return
		val player = entity as? Player ?: return
		if (!player.isHolding(this)) return
		player.rayCast(50.0, blocks())?.let { hit ->
			val block = this.lidarBlocks.getOrPut(hit.blockPosition) { LidarBlock(hit.blockPosition) }
			val colors = arrayOf(Color.GREEN, Color.YELLOW, Color.RED)
			if (block.sides[hit.hitSide] == null) {
				block.getOrInitializeSide(hit.hitSide)
				RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_WEATHER, { event, _ ->
					val blockPos = hit.blockPosition.toVec3()
					val poseStack = event.poseStack
					val side = block.getOrInitializeSide(hit.hitSide)
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(blockPos, event.camera, false)
					poseStack.translateToSide(hit.hitSide)
					for (index in side.indices) {
						val long = side[index]
						for (b in 0 ..< Long.SIZE_BITS) {
							poseStack.pushPose()
							if ((long ushr b) and 1L != 0L) {
								val absolute = (index * Long.SIZE_BITS) + b
								val pos = this.getPixelPosForLongs(hit.hitSide, absolute)
								val distance = player.position().distanceTo(hit.hitPosition.plus(pos))
								val color = if (distance < 5) colors[2] else if (distance < 15) colors[1] else colors[0]
								poseStack.translate(pos)
								poseStack.scaleFlat(1 / 16f)
								drawQuad(poseStack, renderType = ModRenderType.LIDAR, color = color)
							}
							poseStack.popPose()
						}
					}
					poseStack.popPose()
					false
				})
			} else {
				val absoluteIndex = this.getPixelIndex(hit)
				block.setBitForIndex(absoluteIndex, hit.hitSide, true)
			}
		}
		super.inventoryTick(stack, level, entity, slotId, isSelected)
	}

	override fun renderLevelStageEvent(
		event: RenderLevelStageEvent,
		bufferSource: MultiBufferSource,
		player: LocalPlayer
	) {
		if (event.stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			val poseStack = event.poseStack
			poseStack.pushPose()
			event.camera.raycast(50.0, blocks())?.let { hit ->
				val block = this.lidarBlocks[hit.blockPosition] ?: return@let
				val blockPos = hit.blockPosition.toVec3()
				poseStack.offsetRenderToCameraPos(blockPos, event.camera, false)
				poseStack.translateToSide(hit.hitSide)
				poseStack.translate(this.getRawPixelPos(hit))
				val absoluteIndex = this.getPixelIndex(hit)
				val index = block.getArrayIndex(absoluteIndex)
				displayClientMessage(
					"index: $index[${block.getBitString(block.getLong(index, hit.hitSide))}]"
				)
				poseStack.scaleFlat(1 / 16f)
				drawQuad(poseStack, renderType = ModRenderType.LIDAR)
			}
			poseStack.popPose()
		}
	}
}