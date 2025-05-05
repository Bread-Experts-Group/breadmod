package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.WEST
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderStaticItem
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity

class ToasterRenderer(
	context: Context
) : BreadModBER<ToasterBlockEntity>(
	context
) {
	private companion object {
		val HANDLE_MODEL: BakedModel = localClient.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
	}

	private val itemRenderer: ItemRenderer = this.context.itemRenderer

	override fun render(
		blockEntity: ToasterBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val triggered = blockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)

		poseStack.pushPose()
		this.renderOriginalModel(blockEntity, poseStack, bufferSource, packedOverlay)
		when (blockRotation) {
			SOUTH -> {
				poseStack.mulPose(Axis.YP.rotationDegrees(180f))
				poseStack.translate(-1.0, 0.0, -1.0)
			}
			WEST  -> {
				poseStack.mulPose(Axis.YP.rotationDegrees(90f))
				poseStack.translate(-1.0, 0.0, 0.0)
			}
			EAST  -> {
				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
				poseStack.translate(0.0, 0.0, -1.0)
			}
			else  -> {}
		}
		poseStack.translate(0.0, if (triggered) -0.13 else 0.0, 0.0)
		this.renderModel(blockEntity, Companion.HANDLE_MODEL, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
		val stack = blockEntity.getItem(0)

		poseStack.pushPose()
		poseStack.translate(0.5, 0.3, 0.61)
		poseStack.scaleFlat(0.6f)
		if (blockRotation == SOUTH || blockRotation == NORTH) {
			poseStack.mulPose(Axis.YN.rotationDegrees(90f))
			poseStack.translate(-0.185, 0.0, 0.185)
		}
		if (!triggered) {
			if (stack.count == 2) {
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
				poseStack.translate(0.0, 0.0, -0.37)
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
			} else this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
		}
		poseStack.popPose()
	}

	override fun getViewDistance(): Int = 32
}