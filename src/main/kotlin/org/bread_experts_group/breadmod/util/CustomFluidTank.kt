package org.bread_experts_group.breadmod.util

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.IFluidTank
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import java.util.function.Predicate
import kotlin.math.min

open class CustomFluidTank(
    capacity: Int,
    tanks: Int,
    validator: Predicate<FluidStack>
) : IFluidHandler, IFluidTank {
    constructor(capacity: Int, tanks: Int) : this(capacity, tanks, { true })
    constructor(capacity: Int) : this(capacity, 1, { true })

    val tanks: ArrayList<FluidStack> = arrayListOf()
    val capacities: ArrayList<Int> = arrayListOf()

    // todo individual validators for each tank?
    var validator: Predicate<FluidStack>

    init {
        repeat(tanks) {
            this.tanks.add(FluidStack.EMPTY)
            this.capacities.add(capacity)
        }
        this.validator = validator
    }

    override fun getTanks(): Int = tanks.size

    override fun getFluidInTank(tank: Int): FluidStack = tanks[tank]

    fun setFluidInTank(tank: Int, stack: FluidStack) {
        tanks[tank] = stack
    }

    /**
     * Sets the capacity for a specific tank
     */
    fun setTankCapacity(tank: Int, amount: Int) {
        capacities[tank] = amount
    }

    /**
     * Sets the capacity for all tanks
     */
    fun setCapacity(amount: Int) = repeat(capacities.size) { capacities[it] = amount }

    /**
     * @return True if all tanks are empty.
     */
    fun isEmpty() = tanks.all { it.isEmpty }
    fun isEmpty(tank: Int) = tanks[tank].isEmpty

    override fun getTankCapacity(tank: Int): Int = capacities[tank]

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = validator.test(getFluidInTank(tank))

    override fun isFluidValid(stack: FluidStack): Boolean = validator.test(stack)

    // todo these three functions don't account for specific tanks, need our own functions that allow specific tanks
    //  these are also straight copies from FluidTank
    override fun fill(resource: FluidStack, action: FluidAction): Int {
        if (resource.isEmpty || !isFluidValid(resource)) {
            return 0
        }
        if (action.simulate()) {
            if (fluid.isEmpty) {
                return min(capacity.toDouble(), resource.amount.toDouble()).toInt()
            }
            if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
                return 0
            }
            return min((capacity - fluid.amount).toDouble(), resource.amount.toDouble()).toInt()
        }
        if (fluid.isEmpty) {
            tanks[0] = resource.copyWithAmount(min(capacity.toDouble(), resource.amount.toDouble()).toInt())
            onContentsChanged()
            return fluid.amount
        }
        if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return 0
        }
        var filled = capacity - fluid.amount

        if (resource.amount < filled) {
            fluid.grow(resource.amount)
            filled = resource.amount
        } else {
            fluid.amount = capacity
        }
        if (filled > 0) onContentsChanged()
        return filled
    }

    override fun drain(maxDrain: Int, action: FluidAction): FluidStack {
        var drained = maxDrain
        if (fluid.amount < drained) {
            drained = fluid.amount
        }
        val stack = fluid.copyWithAmount(drained)
        if (action.execute() && drained > 0) {
            fluid.shrink(drained)
            onContentsChanged()
        }
        return stack
    }

    override fun drain(resource: FluidStack, action: FluidAction): FluidStack {
        if (resource.isEmpty || !FluidStack.isSameFluidSameComponents(resource, fluid)) return FluidStack.EMPTY
        return drain(resource.amount, action)
    }

    /**
     * @return the first tank's fluid
     */
    override fun getFluid(): FluidStack = tanks[0]

    /**
     * @return The combined amount of all tanks.
     */
    override fun getFluidAmount(): Int {
        var amount = 0
        tanks.forEach {
            amount += it.amount
        }
        return amount
    }

    /**
     * @return The combined capacity of all tanks.
     */
    override fun getCapacity(): Int {
        var capacity = 0
        capacities.forEach {
            capacity += it
        }
        return capacity
    }

    protected fun onContentsChanged() {}
}