package org.bread_experts_group.breadmod.registry.block.actual
//class RadioBlock : BreadModBlockWithEntity(
//	Properties.of()
//		.strength(4f, 6f)
//		.mapColor(MapColor.COLOR_GRAY)
//		.requiresCorrectToolForDrops()
//		.sound(SoundType.METAL)
//) {
//	override fun useWithoutItem(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		hitResult: BlockHitResult
//	): InteractionResult {
//		if (level.isClientSide) localClient.setScreen(RadioScreen(pos))
//		return InteractionResult.sidedSuccess(level.isClientSide)
//	}
//
//	override fun useItemOnBM(
//		stack: ItemStack,
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		hand: InteractionHand,
//		hitResult: BlockHitResult
//	): ItemInteractionResult {
//		if (stack.`is`(Items.STICK)) {
//			val entity = level.getBlockEntity(pos) as RadioBlockEntity
//			if (entity.displayFlip == 90f) entity.displayFlip = -90f else entity.displayFlip = 90f
//			return ItemInteractionResult.sidedSuccess(level.isClientSide)
//		}
//		return super.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
//	}
//
//	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
//		builder.add(BlockStateProperties.HORIZONTAL_FACING)
//	}
//
//	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
//		this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
//
//	override fun onDestroyedByPlayer(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		willHarvest: Boolean,
//		fluid: FluidState
//	): Boolean {
//		StereoSoundInstance.destroy(level.isClientSide, pos)
//		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
//	}
//
//	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
//		ModBlockEntityTypes.RADIO.get()
//
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RadioBlockEntity(pos, state)
//	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
//	override fun codec(): MapCodec<out BaseEntityBlock> = simpleCodec { this }
//}