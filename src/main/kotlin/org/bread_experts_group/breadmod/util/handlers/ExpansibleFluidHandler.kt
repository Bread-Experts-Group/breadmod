package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.IFluidTank
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import org.bread_experts_group.breadmod.util.capInt
import java.math.BigDecimal
import java.util.function.Predicate

@Suppress("unused")
class ExpansibleFluidHandler(
	tanks: List<ExpansibleTank>,
	var receiveAction: (count: BigDecimal, simulate: IFluidHandler.FluidAction, tankIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	var extractAction: (count: BigDecimal, simulate: IFluidHandler.FluidAction, tankIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	val itemContainer: ItemStack = ItemStack.EMPTY
) : IFluidHandler, IFluidHandlerItem {
	private val tanks: MutableList<ExpansibleTank> = tanks.toMutableList()
	fun getTank(tank: Int): ExpansibleTank = this.tanks[tank]

	class ExpansibleTank(
		var capacity: BigDecimal,
		var maxIn: BigDecimal? = null,
		var maxOut: BigDecimal? = null,
		var filter: Predicate<FluidStack> = Predicate { _ -> true },
		var fluid: Fluid = Fluids.EMPTY,
		var amount: BigDecimal = BigDecimal.ZERO
	) : IFluidTank {
		constructor(
			capacity: Int,
			allowIn: Boolean,
			allowOut: Boolean,
			filter: Predicate<FluidStack> = Predicate { _ -> true },
			fluid: Fluid = Fluids.EMPTY,
			amount: BigDecimal = BigDecimal.ZERO
		) : this(
			capacity.toBigDecimal(),
			if (allowIn) capacity.toBigDecimal() else null,
			if (allowOut) capacity.toBigDecimal() else null,
			filter,
			fluid,
			amount
		)

		val isEmpty: Boolean
			get() = this.amount == BigDecimal.ZERO
		val fluidType: FluidType
			get() = this.fluid.fluidType

		override fun getFluid(): FluidStack = FluidStack(this.fluid, this.fluidAmount)
		override fun getFluidAmount(): Int = this.amount.capInt()
		override fun getCapacity(): Int = this.capacity.capInt()
		override fun isFluidValid(stack: FluidStack): Boolean = this.filter.test(stack)

		fun fillDecimal(
			count: BigDecimal,
			action: IFluidHandler.FluidAction
		): BigDecimal {
			val actualCount = if (this.maxIn != null) count.min(this.maxIn) else count
			val saved = this.amount
			val sum = (saved + actualCount).min(this.capacity)
			if (action == IFluidHandler.FluidAction.EXECUTE) this.amount = sum
			return sum - saved
		}

		override fun fill(
			stack: FluidStack,
			action: IFluidHandler.FluidAction
		): Int = this.fillDecimal(
			stack.amount.toBigDecimal(),
			action
		).capInt()

		override fun drain(
			count: Int,
			action: IFluidHandler.FluidAction
		): FluidStack {
			TODO("Not yet implemented")
		}

		override fun drain(
			stack: FluidStack,
			action: IFluidHandler.FluidAction
		): FluidStack {
			TODO("Not yet implemented")
		}
	}

	override fun fill(
		stack: FluidStack,
		action: IFluidHandler.FluidAction
	): Int {
		var count = stack.amount
		return this.tanks.sumOf {
			val filled = it.fill(FluidStack(stack.fluid, count), action)
			count -= filled
			filled
		}
	}

	override fun drain(
		stack: FluidStack,
		action: IFluidHandler.FluidAction
	): FluidStack {
		TODO("Not yet implemented")
	}

	override fun drain(
		stack: Int,
		action: IFluidHandler.FluidAction
	): FluidStack {
		TODO("Not yet implemented")
	}

	override fun getTanks(): Int = this.tanks.size
	override fun getFluidInTank(tank: Int): FluidStack = this.tanks[tank].getFluid()
	override fun getTankCapacity(tank: Int): Int = this.tanks[tank].getCapacity()
	override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = this.tanks[tank].isFluidValid(stack)
	override fun getContainer(): ItemStack = this.itemContainer

	fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
		this.tanks.forEachIndexed { index, tank ->
			tag.put("$index", CompoundTag().also { tankTag ->
				tankTag.putString("fluid", tank.fluid::class.qualifiedName!!)
				tankTag.putString("amount", tank.amount.toEngineeringString())
				tankTag.putString("capacity", tank.amount.toEngineeringString())
			})
		}
	}

	fun deserializeNBT(from: CompoundTag) {
		from.allKeys.forEach {
			val tank = this.tanks[it.toInt()]
			val fluidId = from.getCompound(it).getString("fluid")
			tank.fluid = BuiltInRegistries.FLUID.first { it::class.qualifiedName == fluidId }
			tank.capacity = BigDecimal(from.getCompound(it).getString("capacity"))
			tank.amount = BigDecimal(from.getCompound(it).getString("amount"))
		}
	}
}