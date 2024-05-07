package breadmod.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.tags.TagKey
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import net.minecraftforge.fluids.capability.templates.FluidTank

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
abstract class FluidContainer(val tanks: MutableMap<FluidTank, TankFlow>): IFluidHandler, INBTSerializable<CompoundTag> {
    enum class TankFlow {
        BIDIRECTIONAL,
        FILL_ONLY,
        DRAIN_ONLY
    }

    val allTanks: List<FluidTank>
        get() = tanks.keys.toList()
    fun tanksWithFlow(flow: TankFlow?) =
        if(flow != null) tanks.filter { it.value == flow || it.value == TankFlow.BIDIRECTIONAL }.keys.toList()
        else allTanks

    fun filterFluid(fluid: Fluid, includeEmpty: Boolean, flow: TankFlow? = null) = tanksWithFlow(flow)
        .filter { it.fluid.fluid.isSame(fluid) || (includeEmpty && it.isEmpty) }
    fun filterFluid(fluid: TagKey<Fluid>, includeEmpty: Boolean, flow: TankFlow? = null) = tanksWithFlow(flow)
        .filter { it.fluid.fluid.`is`(fluid) || (includeEmpty && it.isEmpty) }

    fun amount(fluid: Fluid, flow: TankFlow? = null) = filterFluid(fluid, false, flow).sumOf { it.fluidAmount }
    fun amount(fluid: TagKey<Fluid>, flow: TankFlow? = null) = filterFluid(fluid, false, flow).sumOf { it.fluidAmount }
    fun capacity(fluid: Fluid, flow: TankFlow? = null) = filterFluid(fluid, true, flow).sumOf { it.capacity }
    fun capacity(fluid: TagKey<Fluid>, flow: TankFlow? = null) = filterFluid(fluid, true, flow).sumOf { it.capacity }
    fun space(fluid: Fluid, flow: TankFlow? = null) = filterFluid(fluid, true, flow).sumOf { it.space }
    fun space(fluid: TagKey<Fluid>, flow: TankFlow? = null) = filterFluid(fluid, true, flow).sumOf { it.space }

    fun contains(fluid: FluidStack, onlyCheckFluid: Boolean, flow: TankFlow? = null) =
        tanksWithFlow(flow).firstOrNull { it.fluid == fluid && (!onlyCheckFluid || it.fluid.amount >= fluid.amount) }
    fun contains(fluid: Fluid, flow: TankFlow? = null) = tanksWithFlow(flow).firstOrNull { it.fluid.fluid.isSame(fluid) }
    fun contains(fluid: TagKey<Fluid>, flow: TankFlow? = null) = tanksWithFlow(flow).firstOrNull { it.fluid.fluid.`is`(fluid) }

    final override fun getTanks(): Int = allTanks.size
    fun getTanks(flow: TankFlow? = null) = tanksWithFlow(flow).size

    final override fun getFluidInTank(tank: Int): FluidStack = allTanks[tank].fluid.copy()

    abstract fun contentsChanged()

    override fun getTankCapacity(tank: Int): Int = allTanks[tank].capacity
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = allTanks[tank].isFluidValid(stack)
    override fun fill(resource: FluidStack?, action: FluidAction): Int {
        if(resource == null || resource.isEmpty) return 0
        val resourceCopy = resource.copy()
        var filledTotal = 0
        for(tank in tanksWithFlow(TankFlow.FILL_ONLY).filter { it.isFluidValid(resourceCopy) && (it.isEmpty || it.fluid.fluid.isSame(resourceCopy.fluid)) && it.space > 0 }) {
            if(resourceCopy.isEmpty) break
            val filledAmount = tank.fill(resource, action)
            resourceCopy.amount -= filledAmount
            filledTotal += filledAmount
        }
        if(action.execute() && filledTotal > 0) contentsChanged()
        return filledTotal
    }

    override fun drain(resource: FluidStack?, action: FluidAction): FluidStack {
        if(resource == null || resource.isEmpty) return FluidStack.EMPTY
        val resourceCopy = resource.copy()
        val drainedTotal = FluidStack(resource.fluid, 0)
        for(tank in tanksWithFlow(TankFlow.DRAIN_ONLY).filter { !it.isEmpty && it.fluid.fluid.isSame(resourceCopy.fluid) }) {
            if(resourceCopy.isEmpty) break
            val filledAmount = tank.drain(resourceCopy, action)
            resourceCopy.amount -= filledAmount.amount
            drainedTotal.amount += filledAmount.amount
        }
        if(action.execute() && drainedTotal.amount > 0) contentsChanged()
        return drainedTotal
    }

    fun drain(resource: TagKey<Fluid>, amount: Int, action: FluidAction): FluidStack {
        if(amount <= 0) return FluidStack.EMPTY
        var drainedTotal: FluidStack = FluidStack.EMPTY; var remainingAmount = amount
        for(tank in tanksWithFlow(TankFlow.DRAIN_ONLY).filter { !it.isEmpty && it.fluid.fluid.`is`(resource) }) {
            if(drainedTotal.isEmpty) {
                drainedTotal = tank.drain(FluidStack(tank.fluid.fluid, remainingAmount), action)
                remainingAmount -= drainedTotal.amount
            } else {
                val drained = tank.drain(FluidStack(drainedTotal.fluid, remainingAmount), action).amount
                drainedTotal.grow(drained)
                remainingAmount -= drained
            }
            if(remainingAmount <= 0) break
        }
        if(action.execute() && drainedTotal.amount > 0) contentsChanged()
        return drainedTotal
    }

    override fun drain(maxDrain: Int, action: FluidAction): FluidStack =
        drain(tanksWithFlow(TankFlow.DRAIN_ONLY).firstOrNull { !it.isEmpty }?.fluid?.let { FluidStack(it, maxDrain) }, action)

    override fun serializeNBT(): CompoundTag = CompoundTag().also {
        var index = 0
        tanks.forEach { (tank, flow) ->
            it.put(index.toString(), CompoundTag().also { tank.writeToNBT(it); it.putString("FlowType", flow.name) })
            index++
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        var index = 0
        tanks.forEach { (tank, _) ->
            // Probably not deterministic... oh well
            tank.readFromNBT(nbt.getCompound(index.toString()))
            index++
        }
    }

    companion object {
        fun FluidTank.drain(resource: TagKey<Fluid>, amount: Int, action: FluidAction): FluidStack {
            return if(this.fluid.fluid.`is`(resource)) this.drain(amount, action)
            else FluidStack.EMPTY
        }
    }
}