package breadmod.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank

abstract class FluidContainer(size: Int, init: (index: Int) -> FluidTank): IFluidHandler, INBTSerializable<CompoundTag> {
    val tanks: MutableList<FluidTank> = MutableList(size, init)
    val totalCapacity: Int
        get() = tanks.sumOf { it.capacity }


    fun filterFluid(fluid: Fluid, includeEmpty: Boolean) = tanks.filter { it.fluid.fluid.isSame(fluid) || (includeEmpty && it.isEmpty) }

    fun amount(fluid: Fluid) = filterFluid(fluid, false).sumOf { it.fluidAmount }
    fun capacity(fluid: Fluid) = filterFluid(fluid, true).sumOf { it.capacity }
    fun space(fluid: Fluid) = filterFluid(fluid, true).sumOf { it.space }

    fun contains(fluid: FluidStack, onlyCheckFluid: Boolean) = tanks.firstOrNull { it.fluid == fluid && (!onlyCheckFluid || it.fluid.amount >= fluid.amount) }
    fun contains(fluid: Fluid) = tanks.firstOrNull { it.fluid == fluid }

    final override fun getTanks(): Int = tanks.size
    final override fun getFluidInTank(tank: Int): FluidStack = tanks[tank].fluid.copy()

    abstract fun contentsChanged()

    override fun getTankCapacity(tank: Int): Int = tanks[tank].capacity
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = tanks[tank].isFluidValid(stack)
    override fun fill(resource: FluidStack?, action: IFluidHandler.FluidAction): Int {
        if(resource == null || resource.isEmpty) return 0
        var filledTotal = 0
        for(tank in tanks.filter { it.isFluidValid(resource) && (it.isEmpty || it.fluid.fluid.isSame(resource.fluid)) && it.space > 0 }) {
            if(resource.isEmpty) break
            val filledAmount = tank.fill(resource, action)
            resource.amount -= filledAmount
            filledTotal += filledAmount
        }
        if(action.execute() && filledTotal > 0) contentsChanged()
        return filledTotal
    }

    override fun drain(resource: FluidStack?, action: IFluidHandler.FluidAction): FluidStack {
        if(resource == null || resource.isEmpty) return FluidStack.EMPTY
        val drainedTotal = FluidStack(resource.fluid, 0)
        for(tank in tanks.filter { !it.isEmpty && it.fluid.fluid.isSame(resource.fluid) }) {
            if(resource.isEmpty) break
            val filledAmount = tank.drain(resource, action)
            resource.amount -= filledAmount.amount
            drainedTotal.amount += filledAmount.amount
        }
        if(action.execute() && drainedTotal.amount > 0) contentsChanged()
        return drainedTotal
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack =
        drain(tanks.firstOrNull { !it.isEmpty }?.fluid?.let { FluidStack(it, maxDrain) }, action)

    override fun serializeNBT(): CompoundTag = CompoundTag().also {
        tanks.forEachIndexed { index, tank ->
            it.put(index.toString(), CompoundTag().also { tank.writeToNBT(it) })
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        nbt.allKeys.forEach {
            tanks[it.toInt()].readFromNBT(nbt.getCompound(it))
        }
    }
}