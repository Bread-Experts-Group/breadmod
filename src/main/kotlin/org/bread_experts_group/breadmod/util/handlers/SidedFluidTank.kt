package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import java.util.function.Predicate
import kotlin.math.min

// todo proof of concept in progress..
class SidedFluidTank(
	val tanks: List<CustomHandler>
) {
	fun getFluidInTank(tank: Int): FluidStack = this.tanks[tank].fluid
	fun writeToNBT(lookupProvider: Provider, nbt: CompoundTag): CompoundTag {
		val tag = CompoundTag()
		repeat(this.tanks.size) { index ->
			if (!this.tanks[index].fluid.isEmpty) {
				tag.put("Fluid_$index", this.tanks[index].fluid.save(lookupProvider))
			}
		}
		nbt.put("fluids", tag)
		return nbt
	}

	fun readFromNBT(lookupProvider: Provider, nbt: CompoundTag): SidedFluidTank {
		val tag = nbt.getCompound("fluids")
		repeat(this.tanks.size) { index ->
			this.tanks[index].fluid = FluidStack.parseOptional(lookupProvider, tag.getCompound("Fluid_$index"))
		}
		return this
	}

	fun getCapacity(tank: Int): Int = this.tanks[tank].capacity
	open class CustomHandler(
		/**
		 * Returns the capacity of this tank.
		 */
		val capacity: Int,
		private val fluidTanks: Int,
		private val canFill: Boolean,
		private val canDrain: Boolean,
		private val validator: Predicate<FluidStack>
	) : IFluidHandler {
		constructor(capacity: Int, canFill: Boolean, canDrain: Boolean) : this(
			capacity,
			1,
			canFill,
			canDrain,
			{ true }
		)

		override fun getTanks(): Int = this.fluidTanks
		var fluid: FluidStack = FluidStack.EMPTY
		fun isEmpty(): Boolean = this.fluid.amount == 0
		override fun getFluidInTank(tank: Int): FluidStack = this.fluid
		override fun getTankCapacity(tank: Int): Int = this.capacity
		override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = this.isFluidValid(stack)
		fun isFluidValid(stack: FluidStack): Boolean = this.validator.test(stack)
		override fun fill(resource: FluidStack, action: FluidAction): Int {
			if (resource.isEmpty || !this.isFluidValid(resource) || !this.canFill) {
				return 0
			}
			if (action.simulate()) {
				if (this.fluid.isEmpty) {
					return min(this.capacity.toDouble(), resource.amount.toDouble()).toInt()
				}
				if (!FluidStack.isSameFluidSameComponents(this.fluid, resource)) {
					return 0
				}
				return min((this.capacity - this.fluid.amount).toDouble(), resource.amount.toDouble()).toInt()
			}
			if (this.fluid.isEmpty) {
				this.fluid = resource.copyWithAmount(min(this.capacity.toDouble(), resource.amount.toDouble()).toInt())
				this.onContentsChanged()
				return this.fluid.amount
			}
			if (!FluidStack.isSameFluidSameComponents(this.fluid, resource)) {
				return 0
			}
			var filled = this.capacity - this.fluid.amount

			if (resource.amount < filled) {
				this.fluid.grow(resource.amount)
				filled = resource.amount
			} else {
				this.fluid.amount = this.capacity
			}
			if (filled > 0) this.onContentsChanged()
			return filled
		}

		override fun drain(resource: FluidStack, action: FluidAction): FluidStack {
			if (resource.isEmpty || !FluidStack.isSameFluidSameComponents(resource, this.fluid) || !this.canDrain
			) return FluidStack.EMPTY
			return this.drain(resource.amount, action)
		}

		override fun drain(maxDrain: Int, action: FluidAction): FluidStack {
			var drained = maxDrain
			if (this.fluid.amount < drained) {
				drained = this.fluid.amount
			}
			val stack = this.fluid.copyWithAmount(drained)
			if (action.execute() && drained > 0) {
				this.fluid.shrink(drained)
				this.onContentsChanged()
			}
			return stack
		}

		protected open fun onContentsChanged() {}
	}
}