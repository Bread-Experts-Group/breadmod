package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.WEST
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.client.render.renderBlockModel
import org.bread_experts_group.breadmod.client.render.renderStaticItem
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity

class ToasterRenderer(context: Context) : BreadModBER<ToasterBlockEntity>(context) {
	private companion object {
		val HANDLE_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
		val HANDLE_MODEL: BakedModel = localClient.modelManager.getModel(this.HANDLE_MODEL_LOC)
	}

	private var triggeredOffset: Double = 0.0
	private val blockModelRenderer = this.context.blockRenderDispatcher.modelRenderer
	private val itemRenderer = this.context.itemRenderer

	override fun render(
		entity: ToasterBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = entity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val triggered = entity.blockState.getValue(BlockStateProperties.TRIGGERED)

		this.triggeredOffset = if (triggered) -0.13 else 0.0

		poseStack.pushPose()
		when (blockRotation) {
			SOUTH -> {
				poseStack.mulPose(Axis.YN.rotationDegrees(180f))
				poseStack.translate(-1.0, this.triggeredOffset, -1.0)
			}
			WEST  -> {
				poseStack.translate(0.0, this.triggeredOffset, 1.0)
				poseStack.mulPose(Axis.YN.rotationDegrees(-90f))
			}
			EAST  -> {
				poseStack.translate(1.0, this.triggeredOffset, 0.0)
				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
			}
			NORTH -> {
				poseStack.translate(0.0, this.triggeredOffset, 0.0)
			}
			else  -> {}
		}

		this.blockModelRenderer.renderBlockModel(
			poseStack.last(),
			bufferSource,
			entity,
			Companion.HANDLE_MODEL,
			packedLight,
			packedOverlay,
			Sheets.solidBlockSheet()
		)
		poseStack.popPose()
		val stack = entity.itemHandler.getStackInSlot(0)

		poseStack.pushPose()
		poseStack.translate(0.5, 0.3, 0.61)
		poseStack.scaleFlat(0.6f)
		if (blockRotation == SOUTH || blockRotation == NORTH) {
			poseStack.mulPose(Axis.YN.rotationDegrees(90f))
			poseStack.translate(-0.185, 0.0, 0.185)
		}
		if (!triggered) {
			if (stack.count == 2) {
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, entity, packedLight)
				poseStack.translate(0.0, 0.0, -0.37)
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, entity, packedLight)
			} else this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, entity, packedLight)
		}
		poseStack.popPose()
	}

	override fun getViewDistance(): Int = 32
}