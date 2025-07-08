package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.getFluidSpriteAndTint
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DieselGeneratorBlockEntity

class DieselGeneratorRenderer(context: Context) : BreadModBER<DieselGeneratorBlockEntity>(context) {
	override fun renderBM(
		blockEntity: DieselGeneratorBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
	}

	private fun renderFluid(
		blockEntity: DieselGeneratorBlockEntity,
		poseStack: PoseStack,
		rotation: Direction,
		bufferSource: MultiBufferSource
	) {
		val tank = blockEntity.fluidHandler.getUnit(0)
		tank.amount.divide(tank.capacity).toFloat()
		val (_, _) = getFluidSpriteAndTint(tank.fluid, false)
	}
}