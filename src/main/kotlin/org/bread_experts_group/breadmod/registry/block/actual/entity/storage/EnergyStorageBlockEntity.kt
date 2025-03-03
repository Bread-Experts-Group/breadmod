package org.bread_experts_group.breadmod.registry.block.actual.entity.storage

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler.ExpansibleCell

class EnergyStorageBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<EnergyStorageBlockEntity>(
	ModBlockEntityTypes.ENERGY_STORAGE.get(),
	pos,
	state
), EnergyBearingBlockEntity {
	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
		mutableListOf(
			ExpansibleCell(1_000_000, allowIn = true, allowOut = true)
		)
	)
}