package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class DieselGeneratorRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		blockEntity.blockState.getValue(HORIZONTAL_FACING)
		// TODO render fluid
	}
}