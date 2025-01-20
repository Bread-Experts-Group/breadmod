package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.util.handlers.SidedFluidTank

@Suppress("unused")
interface FluidBearingBlockEntity {
	val fluidHandler: SidedFluidTank

	fun growFluid(tank: Int, amount: Int) {
		this.getFluid(tank).grow(amount)
	}

	fun shrinkFluid(tank: Int, amount: Int) {
		this.getFluid(tank).shrink(amount)
	}

	fun getFluid(tank: Int): FluidStack = this.fluidHandler.getFluidInTank(tank)
	fun setFluid(tank: Int, stack: FluidStack) {
		this.fluidHandler.tanks[tank].fluid = stack
	}

	fun serializeFluidsNBT(to: CompoundTag, registries: Provider): Unit = CompoundTag().let {
		this.fluidHandler.writeToNBT(registries, it)
		to.put(this::class.simpleName + "_fluids", it)
		null
	}

	fun deserializeFluidsNBT(from: CompoundTag, registries: Provider) {
		this.fluidHandler.readFromNBT(registries, from.getCompound(this::class.simpleName + "_fluids"))
	}
}