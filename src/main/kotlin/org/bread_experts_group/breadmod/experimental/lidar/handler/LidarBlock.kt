package org.bread_experts_group.breadmod.experimental.lidar.handler

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.render.translateToSide
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.toVec3
import kotlin.math.floor

class LidarBlock(private val blockPos: BlockPos, private val mapColor: Int) {
	private val sides: MutableMap<Direction, Pair<LongArray, Double>> = mutableMapOf()

	private fun getOrPutSide(direction: Direction, zIndex: Double): Pair<LongArray, Double> =
		this.sides.getOrPut(direction) {
			longArrayOf(
				0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
				0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
				0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
				0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000
			) to zIndex
		}

	/**
	 * @param value switches the bit in the long to 1 if true, 0 if false.
	 * @param index the index of the [LongArray] from [sides].
	 */
	fun setPixelForIndexAndSide(index: Int, direction: Direction, zIndex: Double, value: Boolean) {
		val longIndex: Int = index / 64
		val arrayIndex: Int = index - (longIndex * 64)
		val side = this.getOrPutSide(direction, zIndex).first
		val final = (side[longIndex] or (1L shl arrayIndex))
		if ((side[longIndex] ushr arrayIndex) and 1L != 0L) return // Return if the targeted pixel is already filled
		LidarHandler.dotCounter++
		side[longIndex] = if (value) final else final.inv()
	}

	/**
	 * Converts the long in the specified [index] to a human-readable string.
	 */
	fun formatString(index: Int, direction: Direction): String {
		val side = this.sides[direction]?.first ?: return "null"
		return "index: $index[${side[index].toULong().toString(2).padStart(64, '0')}]"
	}

	private fun getPixelPos(
		side: Direction,
		arrayIndex: Int,
		/**
		 * The position of the pixel on the given [side], range is 0 to 255.
		 */
		offset: Int,
		zIndex: Double
	): Vec3 {
		val pixelPos = (arrayIndex * Long.SIZE_BITS) + offset
		val pixelX = floor((pixelPos % 16.0)) / 16
		val pixelY = floor((pixelPos / 16.0)) / 16
		return when (side) {
			DOWN -> Vec3(pixelX, pixelY, zIndex)
			UP -> Vec3(pixelX, -pixelY, zIndex)
			NORTH -> Vec3(pixelX, -pixelY, zIndex)
			SOUTH -> Vec3(pixelX, -pixelY, zIndex)
			WEST -> Vec3(pixelX, -pixelY, zIndex)
			EAST -> Vec3(pixelX, -pixelY, zIndex)
		}
	}

	fun render(poseStack: PoseStack, camera: Camera, consumer: VertexConsumer, forCompile: Boolean) {
		this.sides.forEach { (direction, pair) ->
			val (longArray, zIndex) = pair
			poseStack.pushPose()
			if (!forCompile) poseStack.offsetRenderToCameraPos(this.blockPos.toVec3(), camera, false)
			else poseStack.translate(this.blockPos)
			poseStack.translateToSide(direction)
			for (index in longArray.indices) {
				val long = longArray[index]
				for (bit in 0 ..< Long.SIZE_BITS) {
					poseStack.pushPose()
					// Check if the current bit in SIZE_BITS is 1
					if ((long ushr bit) and 1L != 0L) {
						val pixelPos = this.getPixelPos(direction, index, bit, zIndex)
						poseStack.translate(pixelPos)
						poseStack.scaleFlat(1 / 16f)
						drawQuad(
							poseStack,
							renderType = ModRenderType.LIDAR,
							color = this.mapColor,
							consumer = consumer
						)
						poseStack.translate(1f, 0f, 0f)
						poseStack.mulPose(Axis.YN.rotationDegrees(180f))
						drawQuad(
							poseStack,
							renderType = ModRenderType.LIDAR,
							color = this.mapColor,
							consumer = consumer
						)
					}
					poseStack.popPose()
				}
			}
			poseStack.popPose()
		}
	}
}