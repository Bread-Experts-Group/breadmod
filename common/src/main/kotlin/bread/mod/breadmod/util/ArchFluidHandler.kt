package bread.mod.breadmod.util

import dev.architectury.fluid.FluidStack

interface ArchFluidHandler {
    enum class Action {
        EXECUTE, SIMULATE;

        fun execute() = this == EXECUTE
        fun simulate() = this == SIMULATE
    }

    fun getTanks(): Int

    fun getFluidInTank(tank: Int): FluidStack

    fun getTankCapacity(tank: Int): Int

    fun isFluidValid(stack: FluidStack): Boolean

    fun fill(resource: FluidStack, action: Action): Int

    fun drain(resource: FluidStack, action: Action): FluidStack

    fun drain(maxDrain: Int, action: Action): FluidStack
}
