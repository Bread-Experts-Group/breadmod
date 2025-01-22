package org.bread_experts_group.breadmod.experimental.fluid_tank

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler

class SidedFluidTankJadeBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BlockEntity(ModBlockEntityTypes.FLUID_TANK_JADE_ENTITY.get(), pos, state), FluidBearingBlockEntity {
	override val fluidHandler: ExpansibleFluidHandler = ExpansibleFluidHandler(
		mutableListOf(
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, true),
		)
	)

	override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
		super.getUpdateTag(registries).also { this.saveAdditional(it, registries) }

	override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)
}