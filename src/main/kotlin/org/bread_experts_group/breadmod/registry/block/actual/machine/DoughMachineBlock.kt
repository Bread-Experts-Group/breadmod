package org.bread_experts_group.breadmod.registry.block.actual.machine
//class DoughMachineBlock : BreadModBlockWithEntity(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
//	companion object {
//		val CODEC: MapCodec<DoughMachineBlock> = simpleCodec { DoughMachineBlock() }
//	}
//
//	override fun codec(): MapCodec<DoughMachineBlock> = Companion.CODEC
//	override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean =
//		!player.isCreative
//
//	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
//		this.defaultBlockState()
//			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
//			.setValue(BlockStateProperties.POWERED, false)
//
//	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
//		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED)
//	}
//
//	override fun useWithoutItem(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		hitResult: BlockHitResult
//	): InteractionResult {
//		if (!level.isClientSide) {
//			val entity = level.getBlockEntity(pos) as? DoughMachineBlockEntity ?: return InteractionResult.FAIL
//			player.openMenu(entity, pos)
//		}
//		return InteractionResult.sidedSuccess(level.isClientSide)
//	}
//
//	override fun onRemove(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		newState: BlockState,
//		movedByPiston: Boolean
//	) {
//		if (!state.`is`(newState.block)) {
//			val entity = (level.getBlockEntity(pos) as DoughMachineBlockEntity)
//			entity.dropContents(level, pos)
//		}
//		level.invalidateCapabilities(pos)
//		super.onRemove(state, level, pos, newState, movedByPiston)
//	}
//
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
//		DoughMachineBlockEntity(pos, state)
//
//	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
//		ModBlockEntityTypes.DOUGH_MACHINE.get()
//}