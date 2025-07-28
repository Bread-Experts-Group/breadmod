package org.bread_experts_group.breadmod.registry.block.actual.machine
//class WheatCrusherBlock : BreadModBlockWithEntity(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
//	companion object {
//		val CODEC: MapCodec<WheatCrusherBlock> = simpleCodec { WheatCrusherBlock() }
//	}
//
//	override fun codec(): MapCodec<WheatCrusherBlock> = Companion.CODEC
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
//			val entity = level.getBlockEntity(pos) as? WheatCrusherBlockEntity ?: return InteractionResult.FAIL
//			player.openMenu(entity, pos)
//		}
//		return InteractionResult.sidedSuccess(level.isClientSide)
//	}
//
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
//		WheatCrusherBlockEntity(pos, state)
//
//	override fun onRemove(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		newState: BlockState,
//		movedByPiston: Boolean
//	) {
//		if (!state.`is`(newState.block)) {
//			val entity = (level.getBlockEntity(pos) as WheatCrusherBlockEntity)
//			entity.dropContents(level, pos)
//		}
//		level.invalidateCapabilities(pos)
//		super.onRemove(state, level, pos, newState, movedByPiston)
//	}
//
//	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
//		ModBlockEntityTypes.WHEAT_CRUSHER.get()
//}