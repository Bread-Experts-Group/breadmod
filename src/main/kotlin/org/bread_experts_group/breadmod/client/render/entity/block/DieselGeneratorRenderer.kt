package org.bread_experts_group.breadmod.client.render.entity.block
//
//class DieselGeneratorRenderer(context: Context) : BreadModBER<DieselGeneratorBlockEntity>(context) {
//	override fun renderBM(
//		blockEntity: DieselGeneratorBlockEntity,
//		partialTick: Float,
//		poseStack: PoseStack,
//		bufferSource: MultiBufferSource,
//		packedLight: Int,
//		packedOverlay: Int
//	) {
//		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
//	}
//
//	private fun renderFluid(
//		blockEntity: DieselGeneratorBlockEntity,
//		poseStack: PoseStack,
//		rotation: Direction,
//		bufferSource: MultiBufferSource
//	) {
//		val tank = blockEntity.fluidHandler.getUnit(0)
//		tank.amount.divide(tank.capacity).toFloat()
//		val (_, _) = getFluidSpriteAndTint(tank.fluid, false)
//	}
//}