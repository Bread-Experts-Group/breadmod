package bread.mod.breadmod.util.forge_handlers.fluid

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.FluidStackHooks
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluids
import java.util.function.Predicate

// todo still a work in progress..
//  that means there needs to be support for multiple fluid tanks, each with their own fluid
/**
 * Work in progress implementation of NeoForge's Fluid Tank in common
 */
open class ArchFluidStorage(
    protected var capacity: Int,
    protected var validator: Predicate<FluidStack>?
) : ArchFluidHandler {
    constructor(capacity: Int) : this(capacity, { true })

    var fluid = FluidStack.empty()

    override fun getTanks(): Int = 1

    fun setCapacity(capacity: Int): ArchFluidStorage {
        this.capacity = capacity
        return this
    }

    fun setValidator(validator: Predicate<FluidStack>): ArchFluidStorage {
        if (this.validator != null) {
            this.validator = validator
        }
        return this
    }

    override fun getFluidInTank(tank: Int): FluidStack = fluid

    override fun getTankCapacity(tank: Int): Int = capacity

    override fun isFluidValid(stack: FluidStack): Boolean = validator?.test(stack) == true

    override fun fill(
        resource: FluidStack,
        action: ArchFluidHandler.Action
    ): Int {
        if (resource.isEmpty || !isFluidValid(resource)) {
            return 0
        }
        if (action.simulate()) {
            if (fluid.isEmpty) {
                return minOf(capacity, resource.amount.toInt())
            }
            return minOf(capacity - fluid.amount.toInt(), resource.amount.toInt())
        }
        if (fluid.isEmpty) {
            fluid = resource.copyWithAmount(minOf(capacity, resource.amount.toInt()).toLong())
            onContentsChanged()
            return fluid.amount.toInt()
        }
        var filled = capacity - fluid.amount
        if (resource.amount < filled) {
            fluid.grow(resource.amount)
            filled = resource.amount
        } else {
            fluid.amount = capacity.toLong()
        }
        if (filled > 0) {
            onContentsChanged()
        }
        return filled.toInt()
    }

    override fun drain(
        resource: FluidStack,
        action: ArchFluidHandler.Action
    ): FluidStack {
        return if (resource.isEmpty) FluidStack.empty()
        else drain(resource.amount.toInt(), action)
    }

    override fun drain(
        maxDrain: Int,
        action: ArchFluidHandler.Action
    ): FluidStack {
        var drained = maxDrain
        if (fluid.amount < drained) {
            drained = fluid.amount.toInt()
        }
        val stack = fluid.copyWithAmount(drained.toLong())
        if (action.execute() && drained > 0) {
            fluid.shrink(drained.toLong())
            onContentsChanged()
        }
        return stack
    }

    fun serializeFluid(registries: HolderLookup.Provider, tag: CompoundTag, fluid: FluidStack) {
        if (getFluidInTank(0).fluid != Fluids.EMPTY) {
            tag.putInt("fluid_capacity", capacity)
            tag.put("fluid", FluidStackHooks.write(registries, fluid, tag))
//            LogManager.getLogger().info("saveAdditional fluid: ${fluid.name.string}")
//            LogManager.getLogger().info(tag.getCompound("saveAdditional test: ${tag.getCompound("fluid")}"))
        }
    }

    protected open fun onContentsChanged() {}

    fun deserializeFluid(registries: HolderLookup.Provider, tag: CompoundTag) {
        setCapacity(tag.getInt("fluid_capacity"))
        fluid = FluidStackHooks.readOptional(registries, tag.getCompound("fluid"))
    }
}