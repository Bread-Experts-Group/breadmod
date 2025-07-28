package org.bread_experts_group.breadmod.experimental.fluid_tank
//class SidedFluidTankJadeBlockEntity(
//	pos: BlockPos,
//	state: BlockState
//) : BlockEntity(ModBlockEntityTypes.FLUID_TANK_JADE_ENTITY.get(), pos, state), FluidBearingBlockEntity {
//	override val fluidHandler: ExpansibleFluidHandler = ExpansibleFluidHandler(
//		mutableListOf(
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = true),
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = true),
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = true),
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = true),
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = true),
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = true),
//		)
//	)
//
//	override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
//		super.getUpdateTag(registries).also { this.saveAdditional(it, registries) }
//
//	override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
//		ClientboundBlockEntityDataPacket.create(this)
//}