package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler

@Suppress("unused")
interface EnergyBearingBlockEntity {
	val energyHandler: ExpansibleEnergyHandler

	fun serializeEnergyNBT(registries: HolderLookup.Provider, to: CompoundTag): Unit = CompoundTag().let {
		to.put(this::class.simpleName + "_energy", this.energyHandler.serializeNBT(registries))
	}

	fun deserializeEnergyNBT(registries: HolderLookup.Provider, from: CompoundTag) {
		this.energyHandler.deserializeNBT(registries, from.getCompound(this::class.simpleName + "_energy"))
	}
}