package org.bread_experts_group.breadmod.registry.block.actual.entity.storage
//class EnergyStorageBlockEntity(
//	pos: BlockPos,
//	state: BlockState
//) : BreadModBlockEntity<EnergyStorageBlockEntity>(
//	ModBlockEntityTypes.ENERGY_STORAGE.get(),
//	pos,
//	state
//), EnergyBearingBlockEntity {
//
//	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
//		mutableListOf(
//			ExpansibleCell(1_000_000, allowIn = true, allowOut = true)
//		)
//	)
//
//	override fun commonTick(level: Level) {
//		level.updateStorageLevel(
//			this.blockState, this.blockPos,
//			EnergyStorageBlock.energyToLevel(this.energyHandler.energyStored)
//		)
//	}
//
//	override fun applyImplicitComponents(componentInput: DataComponentInput) {
//		this.energyHandler.receiveEnergy(componentInput.getOrDefault(ModDataComponents.ENERGY, 0), false)
//	}
//
//	override fun collectImplicitComponents(components: Builder) {
//		components.set(ModDataComponents.ENERGY, this.energyHandler.energyStored)
//	}
//
//	private fun Level.updateStorageLevel(state: BlockState, pos: BlockPos, level: Int) =
//		this.setBlockAndUpdate(pos, state.setValue(ModBlockStateProperties.STORAGE_LEVEL, level))
//}