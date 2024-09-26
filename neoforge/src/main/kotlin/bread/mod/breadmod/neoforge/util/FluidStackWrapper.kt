package bread.mod.breadmod.neoforge.util

import bread.mod.breadmod.util.handler.ArchFluidHandler
import bread.mod.breadmod.util.handler.ArchFluidStorage
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class FluidStackWrapper(
    val fluidHandler: ArchFluidStorage
) : IFluidHandler {
    fun translateAction(action: IFluidHandler.FluidAction): ArchFluidHandler.Action =
        if (action.execute()) ArchFluidHandler.Action.EXECUTE else ArchFluidHandler.Action.SIMULATE

    override fun getTanks(): Int = fluidHandler.getTanks()

    override fun getFluidInTank(i: Int): FluidStack =
        FluidStackHooksForge.toForge(fluidHandler.getFluidInTank(i))

    override fun getTankCapacity(i: Int): Int = fluidHandler.getTankCapacity(i)

    override fun isFluidValid(i: Int, fluidStack: FluidStack): Boolean =
        fluidHandler.isFluidValid(FluidStackHooksForge.fromForge(fluidStack))

    override fun fill(
        fluidStack: FluidStack,
        fluidAction: IFluidHandler.FluidAction
    ): Int = fluidHandler.fill(
        FluidStackHooksForge.fromForge(fluidStack),
        translateAction(fluidAction)
    )

    override fun drain(
        fluidStack: FluidStack,
        fluidAction: IFluidHandler.FluidAction
    ): FluidStack = FluidStackHooksForge.toForge(
        fluidHandler.drain(
            FluidStackHooksForge.fromForge(fluidStack),
            translateAction(fluidAction)
        )
    )

    override fun drain(
        i: Int,
        fluidAction: IFluidHandler.FluidAction
    ): FluidStack =
        FluidStackHooksForge.toForge(fluidHandler.drain(i, translateAction(fluidAction)))
}