package org.bread_experts_group.breadmod.registry.block.actual.entity.storage

import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentMap.Builder
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
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
	companion object {
		fun energyToLevel(stored: Int): Int =
			if (stored >= 1000000) 4
			else if (stored > 750000) 3
			else if (stored > 500000) 2
			else if (stored > 250000) 1
			else 0
	}

	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
		mutableListOf(
			ExpansibleCell(1_000_000, allowIn = true, allowOut = true)
		)
	)

	override fun commonTick(level: Level, pos: BlockPos, state: BlockState, entity: EnergyStorageBlockEntity) {
		level.updateStorageLevel(state, pos, Companion.energyToLevel(this.energyHandler.energyStored))
	}

	override fun applyImplicitComponents(componentInput: DataComponentInput) {
		this.energyHandler.receiveEnergy(componentInput.getOrDefault(ModDataComponents.ENERGY, 0), false)
	}

	override fun collectImplicitComponents(components: Builder) {
		components.set(ModDataComponents.ENERGY, this.energyHandler.energyStored)
	}

	private fun Level.updateStorageLevel(state: BlockState, pos: BlockPos, level: Int) =
		this.setBlockAndUpdate(pos, state.setValue(ModBlockStateProperties.STORAGE_LEVEL, level))
}