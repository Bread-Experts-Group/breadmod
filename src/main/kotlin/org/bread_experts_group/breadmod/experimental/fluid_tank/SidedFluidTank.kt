package org.bread_experts_group.breadmod.experimental.fluid_tank

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import java.util.function.Predicate
import kotlin.math.min

// todo proof of concept in progress..
class SidedFluidTank(
    val tanks: ArrayList<CustomHandler>
) {
    fun getFluidInTank(tank: Int) = tanks[tank].fluid

    fun getFluidType(tank: Int) = tanks[tank].fluid.fluidType

    fun setFluidInTank(tank: Int, fluid: FluidStack) {
        tanks[tank].fluid = fluid
    }

    fun getTankCapacity(tank: Int): Int = tanks[tank].capacity

    fun drain(maxDrain: Int, action: FluidAction, tank: Int) = tanks[tank].drain(maxDrain, action)

    fun writeToNBT(lookupProvider: Provider, nbt: CompoundTag): CompoundTag {
        repeat(tanks.size) { index ->
            if (!tanks[index].fluid.isEmpty) {
                nbt.put("Fluid_$index", tanks[index].fluid.save(lookupProvider))
            }
        }

        return nbt
    }

    fun readFromNBT(lookupProvider: Provider, nbt: CompoundTag): SidedFluidTank {
        repeat(tanks.size) { index ->
            tanks[index].fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid_$index"))
        }
        return this
    }

    class CustomHandler(
        val capacity: Int,
        val fluidTanks: Int,
        val validator: Predicate<FluidStack>,
        val contentsChangedListener: () -> Unit
    ) : IFluidHandler {
        constructor(capacity: Int, tanks: Int, listener: () -> Unit) : this(capacity, tanks, { true }, listener)
        constructor(capacity: Int, validator: Predicate<FluidStack>, listener: () -> Unit) : this(
            capacity,
            1,
            validator,
            listener
        )

        constructor(capacity: Int, listener: () -> Unit) : this(capacity, 1, { true }, listener)

        override fun getTanks(): Int = this.fluidTanks
        var fluid = FluidStack.EMPTY

        override fun getFluidInTank(tank: Int): FluidStack = fluid
        fun getFluidAmount(): Int = fluid.amount

        override fun getTankCapacity(tank: Int): Int = this.capacity

        override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = isFluidValid(stack)
        fun isFluidValid(stack: FluidStack) = validator.test(stack)

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
                fluid = resource.copyWithAmount(min(capacity.toDouble(), resource.amount.toDouble()).toInt())
                contentsChangedListener
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
            if (filled > 0) contentsChangedListener
            return filled
        }

        override fun drain(resource: FluidStack, action: FluidAction): FluidStack {
            if (resource.isEmpty || !FluidStack.isSameFluidSameComponents(resource, fluid)) return FluidStack.EMPTY
            return drain(resource.amount, action)
        }

        override fun drain(maxDrain: Int, action: FluidAction): FluidStack {
            var drained = maxDrain
            if (fluid.amount < drained) {
                drained = fluid.amount
            }
            val stack = fluid.copyWithAmount(drained)
            if (action.execute() && drained > 0) {
                fluid.shrink(drained)
                contentsChangedListener
            }
            return stack
        }

//        protected open fun onContentsChanged() = contentsChangedListener
    }
}