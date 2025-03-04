package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import java.awt.Color
import kotlin.jvm.optionals.getOrNull

/**
 * Bread Mod Specific [BlockEntityRenderer] with an extremely cursed "in-world" [GuiGraphics] implementation.
 */
abstract class BreadModBER<T : BreadModBlockEntity<T>>(
	val context: Context,
	private val snapGraphicsToBlockSide: Boolean = true
) : BlockEntityRenderer<T> {
	private companion object {
		const val TRANSLATE_OFFSET = 0.0001
		val LEVEL_GRAPHICS: GuiGraphics =
			object : GuiGraphics(localClient, localClient.renderBuffers().bufferSource()) {
				override fun enableScissor(minX: Int, minY: Int, maxX: Int, maxY: Int) {}
				override fun disableScissor() {}
				override fun containsPointInScissor(x: Int, y: Int): Boolean = false
				override fun fill(
					renderType: RenderType, minX: Int, minY: Int, maxX: Int, maxY: Int, z: Int, color: Int
				) {
					var minX = minX
					var maxX = maxX
					var minY = minY
					var maxY = maxY
					val matrix4f = this.pose().last()
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
					val consumer = this.bufferSource().getBuffer(renderType)
					val plane = z * 0.001f
					consumer.addVertex(matrix4f, minX.toFloat(), minY.toFloat(), plane).setColor(color)
					consumer.addVertex(matrix4f, minX.toFloat(), maxY.toFloat(), plane).setColor(color)
					consumer.addVertex(matrix4f, maxX.toFloat(), maxY.toFloat(), plane).setColor(color)
					consumer.addVertex(matrix4f, maxX.toFloat(), minY.toFloat(), plane).setColor(color)
					this.flush()
				}
			}
	}

	override fun render(
		blockEntity: T,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		Companion.LEVEL_GRAPHICS.pose().last().pose().set(poseStack.last().pose())
		// todo band-aid fix. replace with own innerBlit and override blit methods to use it.
		Companion.LEVEL_GRAPHICS.fill(0, 0, 0, 0, Color(0, 0, 0, 0).rgb)
		if (this.snapGraphicsToBlockSide) this.translateGraphicsToBlockSide(blockEntity)
		this.setupGraphicsPose()
		this.renderWithGraphics(
			blockEntity,
			partialTick,
			poseStack,
			Companion.LEVEL_GRAPHICS.pose(),
			bufferSource,
			Companion.LEVEL_GRAPHICS,
			packedLight,
			packedOverlay
		)
		this.teardownGraphicsPose()
	}

	open fun renderWithGraphics(
		blockEntity: T,
		partialTick: Float,
		poseStack: PoseStack,
		lgPoseStack: PoseStack,
		bufferSource: MultiBufferSource,
		levelGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	/**
	 * Rotates, scales, and positions this [PoseStack] to the proper values.
	 */
	private fun setupGraphicsPose() {
		val lgPoseStack = Companion.LEVEL_GRAPHICS.pose()
		lgPoseStack.pushPose()
		lgPoseStack.mulPose(Axis.ZP.rotationDegrees(180f))
		lgPoseStack.translate(-1f, -1f, 0f)
		lgPoseStack.scaleFlat(0.0625f)
	}

	/**
	 * Returns this [PoseStack] to its original values.
	 */
	private fun teardownGraphicsPose() {
		val lgPoseStack = Companion.LEVEL_GRAPHICS.pose()
		lgPoseStack.mulPose(Axis.ZN.rotationDegrees(180f))
		lgPoseStack.translate(1f, 1f, 0f)
		lgPoseStack.scaleFlat(1f)
		lgPoseStack.popPose()
	}

	private fun translateGraphicsToBlockSide(blockEntity: BlockEntity) {
		val lgPoseStack = Companion.LEVEL_GRAPHICS.pose()
		val facing =
			blockEntity.blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).getOrNull()
				?: blockEntity.blockState.getOptionalValue(BlockStateProperties.FACING).getOrNull() ?: return
		when (facing) {
			DOWN, UP -> {} // todo work on down/up for the FACING property
			NORTH    -> lgPoseStack.translate(0.0, 0.0, -Companion.TRANSLATE_OFFSET)
			SOUTH    -> {
				lgPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
				lgPoseStack.translate(-1.0, 0.0, -1.0 - Companion.TRANSLATE_OFFSET)
			}
			WEST     -> {
				lgPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
				lgPoseStack.translate(-1.0, 0.0, -Companion.TRANSLATE_OFFSET)
			}
			EAST     -> {
				lgPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
				lgPoseStack.translate(0.0, 0.0, -1.0 - Companion.TRANSLATE_OFFSET)
			}
		}
	}
}