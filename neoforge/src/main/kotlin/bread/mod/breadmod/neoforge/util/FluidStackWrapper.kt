package bread.mod.breadmod.neoforge.util

import bread.mod.breadmod.util.ArchFluidHandler
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
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

    fun translateAction(action: IFluidHandler.FluidAction): ArchFluidHandler.Action =
        if (action.execute()) ArchFluidHandler.Action.EXECUTE else ArchFluidHandler.Action.SIMULATE

    override fun getTanks(): Int = fluidEntity.getTanks()

    override fun getFluidInTank(i: Int): FluidStack =
        FluidStackHooksForge.toForge(fluidEntity.getFluidInTank(i))

    override fun getTankCapacity(i: Int): Int = fluidEntity.getTankCapacity(i)

    override fun isFluidValid(i: Int, fluidStack: FluidStack): Boolean =
        fluidEntity.isFluidValid(FluidStackHooksForge.fromForge(fluidStack))

    override fun fill(
        fluidStack: FluidStack,
        fluidAction: IFluidHandler.FluidAction
    ): Int = fluidEntity.fill(
        FluidStackHooksForge.fromForge(fluidStack),
        translateAction(fluidAction)
    )

    override fun drain(
        fluidStack: FluidStack,
        fluidAction: IFluidHandler.FluidAction
    ): FluidStack = FluidStackHooksForge.toForge(
        fluidEntity.drain(
            FluidStackHooksForge.fromForge(fluidStack),
            translateAction(fluidAction)
        )
    )

    override fun drain(
        i: Int,
        fluidAction: IFluidHandler.FluidAction
    ): FluidStack =
        FluidStackHooksForge.toForge(fluidEntity.drain(i, translateAction(fluidAction)))
}