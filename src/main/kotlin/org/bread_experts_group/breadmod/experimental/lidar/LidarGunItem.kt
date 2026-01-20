package org.bread_experts_group.breadmod.experimental.lidar

import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
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
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarBlock
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarHandler
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.HitResult
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.displayClientMessage
import org.bread_experts_group.breadmod.util.normalizeHitLoc
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.raycast
import org.bread_experts_group.breadmod.util.times
import org.bread_experts_group.breadmod.util.toVec3
import org.joml.Vector2d
import kotlin.math.floor
import kotlin.math.round

class LidarGunItem : Item(Properties()), IRenderingItem {
	// todo move to using the LidarHandler with sections
	val lidarBlocks: MutableMap<BlockPos, LidarBlock> = mutableMapOf()

	private fun Double.reverse(): Double = round((1 - this) * 100) / 100

	/**
	 * @return The raw x and y coordinate of the block face before processing.
	 */
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
	// todo z offset for non-full block shapes.
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
		return when (hit.hitSide) {
			DOWN -> Vec3(floorY, floorX + 0.0625, 0.0)
			UP -> Vec3(-floorY + 1 - 0.0625, -floorX, 0.0)
			NORTH -> Vec3(floorX, -floorY, 0.0)
			SOUTH -> Vec3(floorX, -floorY, 0.0)
			WEST -> Vec3(floorY, -floorX, 0.0)
			EAST -> Vec3(floorY, -floorX, 0.0)
		}
	}

	private fun getAbsolutePixelIndex(hit: HitResult<BlockState>): Int {
		val pos = this.getPixelPos(hit).times(16f)
		val x = pos.x.toInt()
		val y = if (pos.y < 0.0) -pos.y.toInt() else pos.y.toInt()
		return Mth.clamp(x, 0, 15) + (Mth.clamp(y, 0, 15) * 16)
	}

	private fun fireLidar(player: Player) {
		val level = player.level()
		if (!level.isClientSide) return
		if (!localClient.options.keyUse.isDown) return
		if (!player.isHolding(this)) return
		player.rayCast(50.0, blocks(), 0.1f)?.let { hit ->
			val section = LidarHandler.getSection(hit.blockPosition)
			section.addRenderer()
			val block = section.getBlock(hit.blockPosition)
			val absoluteIndex = this.getAbsolutePixelIndex(hit)
			block.addRenderer()
			block.setPixelForIndexAndSide(absoluteIndex, hit.hitSide, true)
		}
	}

	private var debug: Boolean = false
	private fun renderLidarTarget(event: RenderLevelStageEvent) {
		if (event.stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			val poseStack = event.poseStack
			poseStack.pushPose()
			event.camera.raycast(50.0, blocks())?.let { hit ->
				val block = LidarHandler.getSection(hit.blockPosition).getBlock(hit.blockPosition)
				val blockPos = hit.blockPosition.toVec3()
				poseStack.offsetRenderToCameraPos(blockPos, event.camera, false)
				poseStack.translateToSide(hit.hitSide)
				poseStack.translate(this.getPixelPos(hit))
				val absoluteIndex = this.getAbsolutePixelIndex(hit)
				// divide the 0..255 index of the pixels to 0..3 index of the arrays
				val arrayIndex = absoluteIndex / 64
				displayClientMessage(block.formatString(arrayIndex, hit.hitSide))
				poseStack.scaleFlat(1 / 16f)
				drawQuad(poseStack, renderType = ModRenderType.LIDAR)
			}
			poseStack.popPose()
		}
	}

	private var lastTimeMillis: Long = System.currentTimeMillis() + 10
	override fun renderLevelStageEvent(
		event: RenderLevelStageEvent,
		bufferSource: MultiBufferSource,
		player: LocalPlayer
	) {
		if (this.debug) this.renderLidarTarget(event)
		// todo doesn't seem like the best way to artificially limit the firing speed
		//  since it seems to cause "bursts" of dots to be drawn and then back to the normal handful,
		//  but it'll work for now.
		val systemTime = System.currentTimeMillis()
		if (systemTime >= this.lastTimeMillis) {
			this.fireLidar(player)
			this.lastTimeMillis += 10
		}
	}
}