package org.bread_experts_group.breadmod.util

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import java.util.function.Predicate

open class CustomFluidTank(
    capacity: Int,
    tanks: Int,
    validator: Predicate<FluidStack>
) : FluidTank(capacity, validator) {
    constructor(capacity: Int, tanks: Int) : this(capacity, tanks, { true })

    val tanks: MutableMap<FluidStack, Pair<Int, Int>> = mutableMapOf()

    init {
        repeat(tanks) { this.tanks[FluidStack.EMPTY] = 0 to capacity }
    }

    override fun getTanks(): Int = tanks.size

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return super.fill(resource, action)
    }
}