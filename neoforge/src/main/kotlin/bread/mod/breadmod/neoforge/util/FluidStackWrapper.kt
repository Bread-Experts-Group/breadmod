package bread.mod.breadmod.neoforge.util

import bread.mod.breadmod.util.ArchFluidHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class FluidStackWrapper(
    level: Level,
    pos: BlockPos
) : IFluidHandler {
    val fluidEntity =
        level.getBlockEntity(pos) as ArchFluidHandler? ?: throw IllegalStateException("no fluid handler at $pos")

    fun tank(i: Int): FluidStack =
        FluidStack(fluidEntity.getFluidInTank(i).fluid, fluidEntity.getFluidInTank(i).amount.toInt())

    fun translateAction(action: IFluidHandler.FluidAction): ArchFluidHandler.Action =
        if (action.execute()) ArchFluidHandler.Action.EXECUTE else ArchFluidHandler.Action.SIMULATE

    override fun getTanks(): Int = fluidEntity.getTanks()

    override fun getFluidInTank(i: Int): FluidStack = tank(i)

    override fun getTankCapacity(i: Int): Int = fluidEntity.getTankCapacity(i)

    override fun isFluidValid(i: Int, fluidStack: FluidStack): Boolean = true

    override fun fill(
        fluidStack: FluidStack,
        fluidAction: IFluidHandler.FluidAction
    ): Int = fluidEntity.fill(
        dev.architectury.fluid.FluidStack.create(fluidStack.fluid, fluidStack.amount.toLong()),
        translateAction(fluidAction)
    )

    override fun drain(
        fluidStack: FluidStack,
        fluidAction: IFluidHandler.FluidAction
    ): FluidStack {
        fluidEntity.drain(
            dev.architectury.fluid.FluidStack.create(fluidStack.fluid, fluidStack.amount.toLong()),
            translateAction(fluidAction)
        )
        return FluidStack(fluidStack.fluid, fluidStack.amount)
    }

    override fun drain(
        i: Int,
        fluidAction: IFluidHandler.FluidAction
    ): FluidStack {
        fluidEntity.drain(i, translateAction(fluidAction))
        // todo fix later.
        return FluidStack.EMPTY
    }
}