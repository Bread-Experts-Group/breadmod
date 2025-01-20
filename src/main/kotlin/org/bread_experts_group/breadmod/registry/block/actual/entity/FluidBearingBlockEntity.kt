package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler

@Suppress("unused")
interface FluidBearingBlockEntity {
	val fluidHandler: ExpansibleFluidHandler

	fun growFluid(tank: Int, amount: Int) {
		this.getFluid(tank).grow(amount)
	}

	fun shrinkFluid(tank: Int, amount: Int) {
		this.getFluid(tank).shrink(amount)
	}

	fun getFluid(tank: Int): FluidStack = this.fluidHandler.getFluidInTank(tank)
	fun setFluid(tank: Int, stack: FluidStack): Unit = this.fluidHandler.getTank(tank).let {
		it.fluid = stack.fluid
		it.amount = stack.amount.toBigDecimal()
	}

	fun serializeFluidsNBT(to: CompoundTag): Unit = CompoundTag().let {
		to.put(this::class.simpleName + "_fluids", this.fluidHandler.serializeNBT())
	}

	fun deserializeFluidsNBT(from: CompoundTag) {
		this.fluidHandler.deserializeNBT(from.getCompound(this::class.simpleName + "_fluids"))
	}
}