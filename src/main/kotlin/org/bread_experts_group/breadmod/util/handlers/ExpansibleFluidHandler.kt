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
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import org.bread_experts_group.breadmod.util.capInt
import java.math.BigDecimal
import java.util.function.Predicate
import kotlin.reflect.full.isSubclassOf

class ExpansibleFluidHandler(
	tanks: List<ExpansibleTank>,
	var receiveAction: (count: BigDecimal, simulate: IFluidHandler.FluidAction, tankIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	var extractAction: (count: BigDecimal, simulate: IFluidHandler.FluidAction, tankIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	val itemContainer: ItemStack = ItemStack.EMPTY
) : IFluidHandler, IFluidHandlerItem {
	init {
		val stackTrace = Thread.currentThread().stackTrace
		val callingLocation = this::class.java.classLoader.loadClass(stackTrace.first {
			it.className != this::class.qualifiedName && !it.className.startsWith("java.")
		}.className)
		if (!callingLocation.kotlin.isSubclassOf(FluidBearingBlockEntity::class))
			throw IllegalStateException("ExpansibleFluidHandler must be used in an FluidBearingBlockEntity")
	}

	private val tanks: MutableList<ExpansibleTank> = tanks.toMutableList()
	fun getTank(tank: Int): ExpansibleTank = this.tanks[tank]

	class ExpansibleTank(
		capacity: BigDecimal,
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

		var capacity: BigDecimal? = capacity
			set(value) {
				field = if (value != null && value <= BigDecimal.ZERO) null else value
			}
		val isEmpty: Boolean
			get() = this.amount == BigDecimal.ZERO
		val fluidType: FluidType
			get() = this.fluid.fluidType
		var asStack: FluidStack
			get() = FluidStack(this.fluid, this.fluidAmount)
			set(value) {
				this.fluid = value.fluid
				this.amount = value.amount.toBigDecimal()
			}

		override fun getFluid(): FluidStack = this.asStack
		override fun getFluidAmount(): Int = this.amount.capInt()
		override fun getCapacity(): Int = this.capacity?.capInt() ?: Int.MAX_VALUE
		override fun isFluidValid(stack: FluidStack): Boolean = this.filter.test(stack)

		fun fillDecimal(
			fluid: Fluid,
			count: BigDecimal,
			action: IFluidHandler.FluidAction
		): BigDecimal {
			if (fluid.fluidType != this.fluidType && !this.isEmpty) return BigDecimal.ZERO
			this.fluid = fluid
			val actualCount = if (this.maxIn != null) count.min(this.maxIn) else count
			val saved = this.amount
			val sum = (saved + actualCount).let { this.capacity?.let { c -> it.min(c) } ?: it }
			if (action == IFluidHandler.FluidAction.EXECUTE) this.amount = sum
			return sum - saved
		}

		override fun fill(
			stack: FluidStack,
			action: IFluidHandler.FluidAction
		): Int = this.fillDecimal(
			stack.fluid,
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

	fun fillDecimal(
		fluid: Fluid,
		count: BigDecimal,
		action: IFluidHandler.FluidAction
	): BigDecimal {
		var actualCount = count
		this.tanks.forEachIndexed { tankIndex, tank ->
			val filled = tank.fillDecimal(
				fluid,
				this.receiveAction(actualCount, action, tankIndex) ?: actualCount,
				action
			)
			actualCount -= filled
		}
		return count - actualCount
	}

	override fun fill(
		stack: FluidStack,
		action: IFluidHandler.FluidAction
	): Int = this.fillDecimal(
		stack.fluid,
		stack.amount.toBigDecimal(),
		action
	).capInt()

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
				tankTag.putString("fluid", tank.fluidType.descriptionId)
				tankTag.putString("amount", tank.amount.toEngineeringString())
				tank.capacity?.let { tankTag.putString("capacity", it.toEngineeringString()) }
			})
		}
	}

	fun deserializeNBT(from: CompoundTag) {
		from.allKeys.forEach {
			val tank = this.tanks[it.toInt()]
			val thisCompound = from.getCompound(it)
			val fluidId = thisCompound.getString("fluid")
			tank.fluid = BuiltInRegistries.FLUID.first { it.fluidType.descriptionId == fluidId }
			tank.capacity =
				if (thisCompound.contains("capacity")) BigDecimal(thisCompound.getString("capacity"))
				else null
			tank.amount = BigDecimal(thisCompound.getString("amount"))
		}
	}
}