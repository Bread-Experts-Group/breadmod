package breadmod.util.capability

import breadmod.util.isTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.tags.TagKey
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import net.minecraftforge.fluids.capability.templates.FluidTank

/**
 * Finer-grain implementation of [IFluidHandler] for use in [CapabilityHolder].
 * @see CapabilityHolder
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Suppress("UNUSED", "MemberVisibilityCanBePrivate", "Deprecation")
class FluidContainer(val tanks: MutableMap<FluidTank, StorageDirection>) : IFluidHandler, ICapabilitySavable<CompoundTag> {
    override var changed: (() -> Unit)? = null

    /**
     * [FluidTank]s controlled by this [FluidContainer].
     * @see FluidTank
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val allTanks: List<FluidTank>
        get() = tanks.keys.toList()

    /**
     * Retrieves a [List] of all [FluidTank] with the specified [direction], or [StorageDirection.BIDIRECTIONAL] [FluidTank]s.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see StorageDirection
     * @return A [List] of [FluidTank]s matching the specified [direction], or are [StorageDirection.BIDIRECTIONAL]
     */
    fun tanksWithDirection(direction: StorageDirection?) =
        if(direction != null) tanks.filter { it.value == direction || it.value == StorageDirection.BIDIRECTIONAL }.keys.toList()
        else allTanks

    /**
     * Retrieves a list of [FluidTank]s with the specified [Fluid].
     * @param fluid The [Fluid] to filter out [FluidTank]s for.
     * @param includeEmpty Whether to include empty [FluidTank]s in the final [List].
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return A list of [FluidTank]s with the specified [Fluid], are empty (if [includeEmpty]), and match the [direction] (if any)
     */
    fun filterFluid(fluid: Fluid, includeEmpty: Boolean, direction: StorageDirection? = null) = tanksWithDirection(direction)
        .filter { it.fluid.fluid.isSame(fluid) || (includeEmpty && it.isEmpty) }
    /**
     * Retrieves a list of [FluidTank]s with [Fluid] under the specified [TagKey].
     * @param fluidTag The [Fluid] [TagKey] to filter out [FluidTank]s for.
     * @param includeEmpty Whether to include empty [FluidTank]s in the final list.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return A list of [FluidTank]s with the specified [Fluid] under [fluidTag], are empty (if [includeEmpty]), and match the [direction] (if any)
     */
    fun filterFluid(fluidTag: TagKey<Fluid>, includeEmpty: Boolean, direction: StorageDirection? = null) = tanksWithDirection(direction)
        .filter { it.fluid.fluid.isTag(fluidTag) || (includeEmpty && it.isEmpty) }

    /**
     * Counts the amount of [fluid] contained within this [FluidContainer]'s [FluidTank]s, optionally filtering out for [direction].
     *
     * **NOTE:** This method *will not* account for empty [FluidTank]s.
     * @param fluid The [Fluid] to count for.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return The amount of the specified [fluid] contained within this [FluidContainer].
     */
    fun amount(fluid: Fluid, direction: StorageDirection? = null) = filterFluid(fluid, false, direction).sumOf { it.fluidAmount }
    /**
     * Counts the amount of [Fluid] under the specified [fluidTag] contained within this [FluidContainer]'s [FluidTank]s,
     * optionally filtering out for [direction].
     *
     * **NOTE:** This method *will not* account for empty [FluidTank]s.
     * @param fluidTag The [Fluid] [TagKey] to count for.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return The amount of fluid under the specified [fluidTag] contained within this [FluidContainer].
     */
    fun amount(fluidTag: TagKey<Fluid>, direction: StorageDirection? = null) = filterFluid(fluidTag, false, direction).sumOf { it.fluidAmount }
    /**
     * Counts the capacity within [FluidTank]s containing this [fluid], optionally filtering out for [direction].
     *
     * **NOTE:** This method *will* account for empty [FluidTank]s.
     * @param fluid The [Fluid] to count for.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return The capacity of [FluidTank]s containing the specified [fluid] (or empty) contained within this [FluidContainer].
     */
    fun capacity(fluid: Fluid, direction: StorageDirection? = null) = filterFluid(fluid, true, direction).sumOf { it.capacity }
    /**
     * Counts the capacity within [FluidTank]s containing [Fluid]s under the specified [fluidTag], optionally filtering out for [direction].
     *
     * **NOTE:** This method *will* account for empty [FluidTank]s.
     * @param fluidTag The [Fluid] [TagKey] to count for.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return The capacity of [FluidTank]s containing [Fluid] under the specified [fluidTag] (or empty) contained within this [FluidContainer].
     */
    fun capacity(fluidTag: TagKey<Fluid>, direction: StorageDirection? = null) = filterFluid(fluidTag, true, direction).sumOf { it.capacity }
    /**
     * Counts the remaining space within [FluidTank]s containing this [fluid], optionally filtering out for [direction].
     *
     * **NOTE:** This method *will* account for empty [FluidTank]s.
     * @param fluid The [Fluid] to count for.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return The amount of space in [FluidTank]s containing the specified [fluid] contained within this [FluidContainer].
     */
    fun space(fluid: Fluid, direction: StorageDirection? = null) = filterFluid(fluid, true, direction).sumOf { it.space }
    /**
     * Counts the remaining space within [FluidTank]s containing [Fluid]s under the specified [fluidTag], optionally filtering out for [direction].
     *
     * **NOTE:** This method *will* account for empty [FluidTank]s.
     * @param fluidTag The [Fluid] [TagKey] to count for.
     * @param direction The [StorageDirection] to filter out [FluidTank]s for.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return The amount of space in [FluidTank]s containing [Fluid] under the specified [fluidTag] contained within this [FluidContainer].
     */
    fun space(fluidTag: TagKey<Fluid>, direction: StorageDirection? = null) = filterFluid(fluidTag, true, direction).sumOf { it.space }

    val space: Int
        get() = allTanks.sumOf { it.space }
    val capacity: Int
        get() = allTanks.sumOf { it.capacity }
    val amount: Int
        get() = allTanks.sumOf { it.fluidAmount }

    fun contains(fluid: FluidStack, onlyCheckFluid: Boolean, direction: StorageDirection? = null) =
        tanksWithDirection(direction).firstOrNull { it.fluid == fluid && (!onlyCheckFluid || it.fluid.amount >= fluid.amount) }
    fun contains(fluid: Fluid, direction: StorageDirection? = null) = tanksWithDirection(direction).firstOrNull { it.fluid.fluid.isSame(fluid) }
    fun contains(fluid: TagKey<Fluid>, direction: StorageDirection? = null) = tanksWithDirection(direction).firstOrNull { it.fluid.fluid.`is`(fluid) }

    override fun getTanks(): Int = allTanks.size
    fun getTanks(direction: StorageDirection? = null) = tanksWithDirection(direction).size

    override fun getFluidInTank(tank: Int): FluidStack = allTanks[tank].fluid.copy()

    override fun getTankCapacity(tank: Int): Int = allTanks[tank].capacity
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = allTanks[tank].isFluidValid(stack)

    var insertFluidCheck: ((resource: FluidStack, action: FluidAction) -> Boolean)? = { _, _ -> true }
    var extractFluidCheck: ((resource: FluidStack, action: FluidAction) -> Boolean)? =  { _, _ -> true }
    var extractFluidTagCheck: ((resource: TagKey<Fluid>, amount: Int, action: FluidAction) -> Boolean)? = { _, _, _ -> true }


    override fun fill(resource: FluidStack?, action: FluidAction): Int {
        if(resource == null || resource.isEmpty || insertFluidCheck?.invoke(resource, action) != true) return 0
        val resourceCopy = resource.copy()
        var filledTotal = 0
        for(tank in tanksWithDirection(StorageDirection.STORE_ONLY).filter { it.isFluidValid(resourceCopy) && (it.isEmpty || it.fluid.fluid.isSame(resourceCopy.fluid)) && it.space > 0 }) {
            if(resourceCopy.isEmpty) break
            val filledAmount = tank.fill(resource, action)
            resourceCopy.amount -= filledAmount
            filledTotal += filledAmount
        }
        if(action.execute() && filledTotal > 0) changed?.invoke()
        return filledTotal
    }

    override fun drain(resource: FluidStack?, action: FluidAction): FluidStack {
        if(resource == null || resource.isEmpty || extractFluidCheck?.invoke(resource, action) != true) return FluidStack.EMPTY
        val resourceCopy = resource.copy()
        val drainedTotal = FluidStack(resource.fluid, 0)
        for(tank in tanksWithDirection(StorageDirection.EMPTY_ONLY).filter { !it.isEmpty && it.fluid.fluid.isSame(resourceCopy.fluid) }) {
            if(resourceCopy.isEmpty) break
            val filledAmount = tank.drain(resourceCopy, action)
            resourceCopy.amount -= filledAmount.amount
            drainedTotal.amount += filledAmount.amount
        }
        if(action.execute() && drainedTotal.amount > 0) changed?.invoke()
        return drainedTotal
    }

    fun drain(resource: TagKey<Fluid>, amount: Int, action: FluidAction): FluidStack {
        if(amount <= 0 || extractFluidTagCheck?.invoke(resource, amount, action) != true) return FluidStack.EMPTY
        var drainedTotal: FluidStack = FluidStack.EMPTY; var remainingAmount = amount
        for(tank in tanksWithDirection(StorageDirection.EMPTY_ONLY).filter { !it.isEmpty && it.fluid.fluid.`is`(resource) }) {
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
        if(action.execute() && drainedTotal.amount > 0) changed?.invoke()
        return drainedTotal
    }

    override fun drain(maxDrain: Int, action: FluidAction): FluidStack =
        drain(tanksWithDirection(StorageDirection.EMPTY_ONLY).firstOrNull { !it.isEmpty }?.fluid?.let { FluidStack(it, maxDrain) }, action)

    override fun serializeNBT(): CompoundTag = CompoundTag().also {
        var index = 0
        tanks.forEach { (tank, direction) ->
            it.put(index.toString(), CompoundTag().also { tank.writeToNBT(it); it.putString("TransitDirection", direction.name) })
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