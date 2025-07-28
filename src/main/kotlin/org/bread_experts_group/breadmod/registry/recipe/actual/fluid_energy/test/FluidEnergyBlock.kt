package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test
//class FluidEnergyBlock : BreadModBlockWithEntity(Properties.of()) {
//	companion object {
//		val CODEC: MapCodec<FluidEnergyBlock> = simpleCodec { FluidEnergyBlock() }
//	}
//
//	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
//	override fun codec(): MapCodec<FluidEnergyBlock> = Companion.CODEC
//
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
//		FluidEnergyBlockEntity(pos, state)
//
//	override fun useWithoutItem(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		hitResult: BlockHitResult
//	): InteractionResult {
//		if (!level.isClientSide) {
//			val entity = level.getBlockEntity(pos) as? FluidEnergyBlockEntity ?: return InteractionResult.FAIL
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
//			val entity = level.getBlockEntity(pos) as FluidEnergyBlockEntity
//			entity.dropContents(level, pos)
//		}
//		level.invalidateCapabilities(pos)
//		super.onRemove(state, level, pos, newState, movedByPiston)
//	}
//
//	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
//		ModBlockEntityTypes.FLUID_ENERGY.get()
//}