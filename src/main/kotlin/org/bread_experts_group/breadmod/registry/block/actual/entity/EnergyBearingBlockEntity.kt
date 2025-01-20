package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.nbt.CompoundTag
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler

@Suppress("unused")
interface EnergyBearingBlockEntity {
	val energyHandler: ExpansibleEnergyHandler

	fun serializeEnergyNBT(to: CompoundTag): Unit = CompoundTag().let {
		to.put(this::class.simpleName + "_energy", this.energyHandler.serializeNBT())
	}

	fun deserializeEnergyNBT(from: CompoundTag) {
		this.energyHandler.deserializeNBT(from.getCompound(this::class.simpleName + "_energy"))
	}
}