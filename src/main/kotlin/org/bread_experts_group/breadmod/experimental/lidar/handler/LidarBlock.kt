package org.bread_experts_group.breadmod.experimental.lidar.handler

import com.mojang.math.Axis
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.render.translateToSide
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.toVec3
import kotlin.math.floor

class LidarBlock(private val blockPos: BlockPos) {
	private val sides: MutableMap<Direction, LongArray> = mutableMapOf()
	private var hasRenderer: Boolean = false

	private fun getOrPutSide(direction: Direction): LongArray = this.sides.getOrPut(direction) {
		longArrayOf(
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000
		)
	}

	fun addRenderer() {
		if (this.hasRenderer) return
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
			this.renderSides(event)
			false
		})
		this.hasRenderer = true
	}

	/**
	 * @param value switches the bit in the long to 1 if true, 0 if false.
	 * @param index the index of the [LongArray] from [sides].
	 */
	fun setPixelForIndexAndSide(index: Int, direction: Direction, value: Boolean): Long {
		val longIndex: Int = index / 64
		val arrayIndex: Int = index - (longIndex * 64)
		val side = this.getOrPutSide(direction)
		val final = (side[longIndex] or (1L shl arrayIndex))
		side[longIndex] = if (value) final else final.inv()
		return side[longIndex]
	}

	/**
	 * Converts the long in the specified [index] to a human-readable string.
	 */
	fun formatString(index: Int, direction: Direction): String =
		"index: $index[${this.getOrPutSide(direction)[index].toULong().toString(2).padStart(64, '0')}]"

	// todo z offset for non-full block shapes.
	private fun getPixelPos(
		side: Direction,
		arrayIndex: Int,
		/**
		 * The position of the pixel on the given [side], range is 0 to 255.
		 */
		offset: Int
	): Vec3 {
		val pixelPos = (arrayIndex * Long.SIZE_BITS) + offset
		val pixelX = floor((pixelPos % 16.0)) / 16
		val pixelY = floor((pixelPos / 16.0)) / 16
		return when (side) {
			DOWN -> Vec3(pixelX, pixelY, 0.0)
			UP -> Vec3(pixelX, -pixelY, 0.0)
			NORTH -> Vec3(pixelX, -pixelY, 0.0)
			SOUTH -> Vec3(pixelX, -pixelY, 0.0)
			WEST -> Vec3(pixelX, -pixelY, 0.0)
			EAST -> Vec3(pixelX, -pixelY, 0.0)
		}
	}

	fun renderSides(event: RenderLevelStageEvent) {
		val poseStack = event.poseStack
		val camera = event.camera
		val playerPos = localClient.player?.position() ?: return
		val blockCenter = this.blockPos.center
		val colors = arrayOf(Color.GREEN, Color.YELLOW, Color.RED)
		this.sides.forEach { (direction, longArray) ->
			poseStack.pushPose()
			poseStack.offsetRenderToCameraPos(this.blockPos.toVec3(), camera, false)
			poseStack.translateToSide(direction)
			for (index in longArray.indices) {
				val long = longArray[index]
				for (bit in 0 ..< Long.SIZE_BITS) {
					poseStack.pushPose()
					// Check if the current bit in SIZE_BITS is 1
					if ((long ushr bit) and 1L != 0L) {
						val pixelPos = this.getPixelPos(direction, index, bit)
						val distance = playerPos.distanceTo(blockCenter.plus(pixelPos))
						val color = if (distance < 5) colors[2] else if (distance < 15) colors[1] else colors[0]
						poseStack.translate(pixelPos)
						poseStack.scaleFlat(1 / 16f)
						drawQuad(poseStack, renderType = ModRenderType.LIDAR, color = color)
						poseStack.translate(1f, 0f, 0f)
						poseStack.mulPose(Axis.YN.rotationDegrees(180f))
						drawQuad(poseStack, renderType = ModRenderType.LIDAR, color = color)
					}
					poseStack.popPose()
				}
			}
			poseStack.popPose()
		}
	}
}