package org.bread_experts_group.breadmod.experimental.fluid_tank

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.IFluidTank
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import java.util.function.Predicate
import kotlin.math.min

open class CustomFluidTank(
	val tankCapacity : Int,
	tanks : Int,
	// todo individual validators for each tank?
	val validator : Predicate<FluidStack>
) : IFluidHandler, IFluidTank {
	constructor(capacity : Int, tanks : Int) : this(capacity, tanks, { true })

	protected val tanks : ArrayList<FluidStack> = arrayListOf()
	protected val capacities : ArrayList<Int> = arrayListOf()

	init {
		repeat(tanks) {
			this.tanks.add(FluidStack.EMPTY)
			this.capacities.add(this.tankCapacity)
		}
	}

	final override fun getTanks() : Int = this.tanks.size
	fun getFluid(index : Int) : FluidStack = this.getFluidInTank(index)
	override fun getFluidInTank(tank : Int) : FluidStack = this.tanks[tank]
	fun setFluidInTank(tank : Int, stack : FluidStack) {
		this.tanks[tank] = stack
	}
	/**
	 * @return True if all tanks are empty.
	 */
	fun isEmpty() : Boolean = this.tanks.all { it.isEmpty }
	fun isEmpty(tank : Int) : Boolean = this.tanks[tank].isEmpty
	override fun getTankCapacity(tank : Int) : Int = this.capacities[tank]
	override fun isFluidValid(tank : Int, stack : FluidStack) : Boolean = this.validator.test(this.getFluidInTank(tank))
	override fun isFluidValid(stack : FluidStack) : Boolean = this.validator.test(stack)
	// todo these three functions don't account for specific tanks, need our own functions that allow specific tanks
	//  these are also straight copies from FluidTank
	override fun fill(resource : FluidStack, action : FluidAction) : Int {
		if (resource.isEmpty || !this.isFluidValid(resource)) {
			return 0
		}
		if (action.simulate()) {
			if (this.fluid.isEmpty) {
				return min(this.capacity.toDouble(), resource.amount.toDouble()).toInt()
			}
			if (!FluidStack.isSameFluidSameComponents(this.fluid, resource)) {
				return 0
			}
			return min((this.capacity - this.fluid.amount).toDouble(), resource.amount.toDouble()).toInt()
		}
		if (this.fluid.isEmpty) {
			this.tanks[0] = resource.copyWithAmount(min(this.capacity.toDouble(), resource.amount.toDouble()).toInt())
			this.onContentsChanged()
			return this.fluid.amount
		}
		if (!FluidStack.isSameFluidSameComponents(this.fluid, resource)) {
			return 0
		}
		var filled = this.capacity - this.fluid.amount

		if (resource.amount < filled) {
			this.fluid.grow(resource.amount)
			filled = resource.amount
		} else {
			this.fluid.amount = this.capacity
		}
		if (filled > 0) this.onContentsChanged()
		return filled
	}
	// todo move logic from this and drainTank to private function to reduce code duplication
	override fun drain(maxDrain : Int, action : FluidAction) : FluidStack {
		var drained = maxDrain
		if (this.fluid.amount < drained) {
			drained = this.fluid.amount
		}
		val stack = this.fluid.copyWithAmount(drained)
		if (action.execute() && drained > 0) {
			this.fluid.shrink(drained)
			this.onContentsChanged()
		}
		return stack
	}

	fun drainTank(maxDrain : Int, action : FluidAction, tank : Int) : FluidStack {
		var drained = maxDrain
		if (this.tanks[tank].amount < drained) {
			drained = this.fluid.amount
		}
		val stack = this.tanks[tank].copyWithAmount(drained)
		if (action.execute() && drained > 0) {
			this.tanks[tank].shrink(drained)
			this.onContentsChanged()
		}
		return stack
	}

	override fun drain(resource : FluidStack, action : FluidAction) : FluidStack {
		if (resource.isEmpty || !FluidStack.isSameFluidSameComponents(resource, this.tanks[1])) return FluidStack.EMPTY
		return this.drain(resource.amount, action)
	}
	/**
	 * @return the first tank's fluid
	 */
	override fun getFluid() : FluidStack = this.tanks[0]
	/**
	 * @return The combined fluid volume of all tanks.
	 */
	override fun getFluidAmount() : Int {
		var amount = 0
		this.tanks.forEach {
			amount += it.amount
		}
		return amount
	}
	//    /**
//     * @return The combined capacity of all tanks.
//     */
//    override fun getCapacity(): Int {
//        var capacity = 0
//        capacities.forEach {
//            capacity += it
//        }
//        return capacity
//    }
	override fun getCapacity() : Int = this.tankCapacity
	protected open fun onContentsChanged() {}
	fun writeToNBT(lookupProvider : Provider, nbt : CompoundTag) : CompoundTag {
		repeat(this.tanks.size) { index ->
			if (!this.tanks[index].isEmpty) {
				nbt.put("Fluid_$index", this.tanks[index].save(lookupProvider))
			}
		}

		return nbt
	}

	fun readFromNBT(lookupProvider : Provider, nbt : CompoundTag) : CustomFluidTank {
		repeat(this.tanks.size) { index ->
			this.tanks[index] = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid_$index"))
		}
		return this
	}
}