package org.bread_experts_group.breadmod.registry.block.actual.entity.machine
//class CreativeGeneratorBlockEntity(
//	pos: BlockPos,
//	state: BlockState
//) : BreadModBlockEntity<CreativeGeneratorBlockEntity>(
//	ModBlockEntityTypes.CREATIVE_GENERATOR.get(),
//	pos,
//	state
//), EnergyBearingBlockEntity {
//	override val energyHandler: ExpansibleEnergyHandler =
//		ExpansibleEnergyHandler(mutableListOf(ExpansibleCell(BigDecimal.valueOf(Long.MAX_VALUE))))
//
//	override fun serverTick(serverLevel: ServerLevel) {
//		val enabled = this.blockState.getValue(CreativeGeneratorBlock.ENABLED)
//		if (enabled) {
//			if (this.energyHandler.energyStored != this.energyHandler.maxEnergyStored) this.energyHandler.receiveEnergy(
//				Int.MAX_VALUE,
//				false
//			)
//			Direction.entries
//				.asSequence()
//				.mapNotNull {
//					serverLevel.getCapability(
//						Capabilities.EnergyStorage.BLOCK,
//						this.blockPos.relative(it),
//						it
//					)
//				}
//				.forEach { it.receiveEnergy(Int.MAX_VALUE, false) }
//		}
//	}
//
//	override fun clientTick(
//		clientLevel: ClientLevel
//	) {
//		val enabled = this.blockState.getValue(CreativeGeneratorBlock.ENABLED)
//		if (clientLevel.gameTime % 80.0 == 0.0 && enabled)
//			clientLevel.playLocalSound(
//				this.blockPos,
//				SoundEvents.BEACON_AMBIENT,
//				BLOCKS,
//				1f, 1f, false
//			)
//	}
//}