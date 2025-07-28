package org.bread_experts_group.breadmod.registry.block.actual.machine
//class DieselGeneratorBlock : BreadModBlockWithEntity(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
//	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
//		builder.add(
//			BlockStateProperties.OPEN,
//			BlockStateProperties.HORIZONTAL_FACING,
//			ModBlockStateProperties.UPGRADE_ONE,
//			ModBlockStateProperties.UPGRADE_TWO,
//			ModBlockStateProperties.UPGRADE_THREE
//		)
//	}
//
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
//		DieselGeneratorBlockEntity(pos, state)
//
//	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
//		this.defaultBlockState()
//			.setValue(BlockStateProperties.OPEN, false)
//			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
//			.setValue(ModBlockStateProperties.UPGRADE_ONE, false)
//			.setValue(ModBlockStateProperties.UPGRADE_TWO, false)
//			.setValue(ModBlockStateProperties.UPGRADE_THREE, false)
//
//}