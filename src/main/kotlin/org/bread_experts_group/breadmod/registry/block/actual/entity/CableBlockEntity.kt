package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.CableBlock
import org.bread_experts_group.breadmod.util.handlers.AbstractExpansibleHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

class CableBlockEntity(
	pos: BlockPos, state: BlockState,
	val capabilities: Map<BlockCapability<*, Direction?>, Any> = mapOf()
) : BlockEntity(
	ModBlockEntityTypes.CABLE.get(),
	pos,
	state
), EnergyBearingBlockEntity, FluidBearingBlockEntity, ItemBearingBlockEntity {
	override val energyHandler: ExpansibleEnergyHandler
		get() = TODO("Not yet implemented")
	override val fluidHandler: ExpansibleFluidHandler
		get() = TODO("Not yet implemented")
	override val itemHandler: ExpansibleItemHandler
		get() = TODO("Not yet implemented")

	override fun setLevel(level: Level) {
		val lambda = {
			loop@ for ((direction, pair) in CableBlock.Companion.directions) {
				if (!this.blockState.getValue(pair.first)) continue
				for ((capability, value) in this.capabilities) {
					val otherCapability = level.getCapability(capability, this.blockPos.relative(direction), direction)
					when (otherCapability) {
						is IEnergyStorage -> {
							value as ExpansibleEnergyHandler
							if (value.energyStored == 0) continue
							value.suppressChanged = true
							val extracted = value.extractEnergy(1, true)
							if (extracted > 0) {
								val putIn = otherCapability.receiveEnergy(extracted, false)
								otherCapability.extractEnergy(putIn, false)
							}
							value.suppressChanged = false
						}
						null              -> continue@loop
						else              -> TODO("${otherCapability::class.qualifiedName} ?: ${value::class.qualifiedName}")
					}
				}
			}
		}
		for ((_, capability) in this.capabilities) {
			capability as AbstractExpansibleHandler<*>
			capability.changed = lambda
		}
		super.setLevel(level)
	}
}