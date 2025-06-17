package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler

@Suppress("unused")
interface FluidBearingBlockEntity {
	val fluidHandler: ExpansibleFluidHandler

	fun growFluid(tank: Int, amount: Int) {
		val fluid = this.getFluid(tank)
		fluid.grow(amount)
		this.setFluid(tank, fluid)
	}

	fun shrinkFluid(tank: Int, amount: Int) {
		val fluid = this.getFluid(tank)
		fluid.shrink(amount)
		this.setFluid(tank, fluid)
	}

	fun getFluid(tank: Int): FluidStack = this.fluidHandler.getFluidInTank(tank)
	fun setFluid(tank: Int, stack: FluidStack): Unit = this.fluidHandler.getUnit(tank).let {
		it.fluid = stack.fluid
		it.amount = stack.amount.toBigDecimal()
	}

	fun getFluidsInRange(range: IntRange): List<FluidStack> = buildList {
		range.forEach { this.add(this@FluidBearingBlockEntity.getFluid(it)) }
	}

	fun setOrGrowFluid(tank: Int, fluid: FluidStack, growAmount: Int): Unit =
		if (this.getFluid(tank).isEmpty) this.setFluid(tank, fluid) else this.growFluid(tank, growAmount)

	fun serializeFluidsNBT(registries: HolderLookup.Provider, to: CompoundTag): Unit = CompoundTag().let {
		to.put(this::class.simpleName + "_fluids", this.fluidHandler.serializeNBT(registries))
	}

	fun deserializeFluidsNBT(registries: HolderLookup.Provider, from: CompoundTag) {
		this.fluidHandler.deserializeNBT(registries, from.getCompound(this::class.simpleName + "_fluids"))
	}
}