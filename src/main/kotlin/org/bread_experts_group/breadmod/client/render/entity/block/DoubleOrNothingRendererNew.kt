package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntityNew

class DoubleOrNothingRendererNew(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntityNew> {
	override fun render(
		blockEntity: DoubleOrNothingBlockEntityNew,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}
}