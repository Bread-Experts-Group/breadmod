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
import org.bread_experts_group.breadmod.util.handlers.HandlerCommon.calculateAndSave
import java.math.BigDecimal
import java.util.function.Predicate
import kotlin.reflect.full.isSubclassOf

class ExpansibleFluidHandler(
	tanks: List<ExpansibleTank>,
	override var receiveAction: (count: BigDecimal, simulate: Boolean, tankIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	override var extractAction: (count: BigDecimal, simulate: Boolean, tankIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	val itemContainer: ItemStack = ItemStack.EMPTY
) : IFluidHandler, IFluidHandlerItem, HandlerListener {
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
		override var maxIn: BigDecimal? = null,
		override var maxOut: BigDecimal? = null,
		var filter: Predicate<FluidStack> = Predicate { _ -> true },
		var fluid: Fluid = Fluids.EMPTY,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : IFluidTank, HandlerLimits {
		constructor(
			capacity: Int,
			allowIn: Boolean,
			allowOut: Boolean,
			filter: Predicate<FluidStack> = Predicate { _ -> true },
			fluid: Fluid = Fluids.EMPTY,
			amount: BigDecimal = BigDecimal.ZERO
		) : this(
			capacity.toBigDecimal(),
			if (allowIn) capacity.toBigDecimal() else BigDecimal.ZERO,
			if (allowOut) capacity.toBigDecimal() else BigDecimal.ZERO,
			filter,
			fluid,
			amount
		)

		override var capacity: BigDecimal? = capacity
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
			simulate: Boolean
		): BigDecimal {
			if ((fluid.fluidType != this.fluidType && !this.isEmpty) || count <= BigDecimal.ZERO) return BigDecimal.ZERO
			this.fluid = fluid
			return this.calculateAndSave(count, simulate)
		}

		override fun fill(
			stack: FluidStack,
			action: IFluidHandler.FluidAction
		): Int = this.fillDecimal(
			stack.fluid,
			stack.amount.toBigDecimal(),
			action == IFluidHandler.FluidAction.SIMULATE
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
		simulate: Boolean
	): BigDecimal {
		var actualCount = count
		for (tankIndex in this.tanks.indices) {
			val tank = this.tanks[tankIndex]
			if (tank.maxIn == BigDecimal.ZERO) continue
			val toFill = this.receiveAction(actualCount, simulate, tankIndex) ?: actualCount
			val filled = tank.fillDecimal(
				fluid,
				tank.maxIn?.min(toFill) ?: toFill,
				simulate
			)
			actualCount -= filled
			if (actualCount == BigDecimal.ZERO) break
		}
		return count - actualCount
	}

	override fun fill(
		stack: FluidStack,
		action: IFluidHandler.FluidAction
	): Int = this.fillDecimal(
		stack.fluid,
		stack.amount.toBigDecimal(),
		action == IFluidHandler.FluidAction.SIMULATE
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