package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import kotlin.jvm.optionals.getOrNull

/**
 * Cursed adaptation of GuiGraphics to work in the level, WIP.
 */
class LevelGraphics : GuiGraphics(
	localClient,
	localClient.renderBuffers().bufferSource()
) {
	override fun containsPointInScissor(x: Int, y: Int): Boolean = TODO("Scissor")
	override fun enableScissor(minX: Int, minY: Int, maxX: Int, maxY: Int) = TODO("Scissor")
	override fun disableScissor() = TODO("Scissor")
	override fun fill(
		renderType: RenderType,
		minX: Int, minY: Int,
		maxX: Int, maxY: Int, z: Int,
		color: Int
	) {
		val matrix4f = this.pose().last()
		var minX = minX.toFloat()
		var maxX = maxX.toFloat()
		var minY = minY.toFloat()
		var maxY = maxY.toFloat()
		if (minX < maxX) {
			val i = minX
			minX = maxX
			maxX = i
		}

		if (minY < maxY) {
			val j = minY
			minY = maxY
			maxY = j
		}
		val plane = z * BreadModBER.TRANSLATE_OFFSET.toFloat()
		val consumer = this.bufferSource().getBuffer(renderType)
		consumer.addVertex(matrix4f, minX, minY, plane).setColor(color)
		consumer.addVertex(matrix4f, minX, maxY, plane).setColor(color)
		consumer.addVertex(matrix4f, maxX, maxY, plane).setColor(color)
		consumer.addVertex(matrix4f, maxX, minY, plane).setColor(color)
		this.flush()
	}

	/**
	 * Rotates, scales, and positions this [PoseStack] to the proper values.
	 */
	fun setup() {
		val lgPoseStack = this.pose()
		lgPoseStack.pushPose()
		lgPoseStack.mulPose(Axis.ZP.rotationDegrees(180f))
		lgPoseStack.translate(-1f, -1f, 0f)
		lgPoseStack.scaleFlat(0.0625f)
	}

	/**
	 * Returns this [PoseStack] to its original values.
	 */
	fun teardown() {
		val lgPoseStack = this.pose()
		lgPoseStack.mulPose(Axis.ZN.rotationDegrees(180f))
		lgPoseStack.translate(1f, 1f, 0f)
		lgPoseStack.scaleFlat(1f)
		lgPoseStack.popPose()
	}

	fun translateToBlockSide(blockEntity: BlockEntity) {
		val lgPoseStack = this.pose()
		val facing =
			blockEntity.blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).getOrNull()
				?: blockEntity.blockState.getOptionalValue(BlockStateProperties.FACING).getOrNull() ?: return
		when (facing) {
			Direction.DOWN, Direction.UP -> {} // todo work on down/up for the FACING property
			Direction.NORTH -> lgPoseStack.translate(0.0, 0.0, -BreadModBER.TRANSLATE_OFFSET)
			Direction.SOUTH -> {
				lgPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
				lgPoseStack.translate(-1.0, 0.0, -1.0 - BreadModBER.TRANSLATE_OFFSET)
			}
			Direction.WEST -> {
				lgPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
				lgPoseStack.translate(-1.0, 0.0, -BreadModBER.TRANSLATE_OFFSET)
			}
			Direction.EAST -> {
				lgPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
				lgPoseStack.translate(0.0, 0.0, -1.0 - BreadModBER.TRANSLATE_OFFSET)
			}
		}
	}
}