package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import org.bread_experts_group.breadmod.registry.block.actual.entity.DieselGeneratorBlockEntity

// todo the rest of the models
class DieselGeneratorRenderer(context: Context) : BreadModBER<DieselGeneratorBlockEntity>(context) {
	override fun render(
		blockEntity: DieselGeneratorBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		poseStack.pushPose()
		this.renderOriginalModel(blockEntity, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
	}
}