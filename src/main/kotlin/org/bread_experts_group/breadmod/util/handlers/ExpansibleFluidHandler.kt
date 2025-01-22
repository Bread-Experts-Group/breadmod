package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.HolderLookup
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
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler.ExpansibleTank
import java.math.BigDecimal
import java.util.function.Predicate
import kotlin.reflect.full.isSubclassOf

class ExpansibleFluidHandler(
	override val units: MutableList<ExpansibleTank>,
	val itemContainer: ItemStack = ItemStack.EMPTY
) : AbstractExpansibleHandler<ExpansibleTank>(), IFluidHandler, IFluidHandlerItem {
	init {
		val stackTrace = Thread.currentThread().stackTrace
		val callingLocation = this::class.java.classLoader.loadClass(stackTrace.first {
			it.className != this::class.qualifiedName && !it.className.startsWith("java.")
		}.className)
		if (!callingLocation.kotlin.isSubclassOf(FluidBearingBlockEntity::class))
			throw IllegalStateException("ExpansibleFluidHandler must be used in an FluidBearingBlockEntity")
	}

	class ExpansibleTank(
		capacity: BigDecimal,
		override var maxIn: BigDecimal? = null,
		override var maxOut: BigDecimal? = null,
		var filter: Predicate<FluidStack> = Predicate { _ -> true },
		var fluid: Fluid = Fluids.EMPTY,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : IFluidTank, HandlerSerializable {
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
			get() = this.fluidType.isAir || this.amount == BigDecimal.ZERO
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

		override fun fillDecimal(
			count: BigDecimal,
			simulate: Boolean,
			additional: MutableList<Any>
		): Pair<BigDecimal, List<Any>> {
			val fluid = additional[0] as Fluid
			if ((fluid.fluidType != this.fluidType && !this.isEmpty) || count <= BigDecimal.ZERO)
				return BigDecimal.ZERO to mutableListOf()
			this.fluid = fluid
			return super.fillDecimal(count, simulate, additional)
		}

		override fun fill(
			stack: FluidStack,
			action: IFluidHandler.FluidAction
		): Int = this.fillDecimal(
			stack.amount.toBigDecimal(),
			action == IFluidHandler.FluidAction.SIMULATE,
			mutableListOf(stack.fluid)
		).first.capInt()

		override fun drain(
			count: Int,
			action: IFluidHandler.FluidAction
		): FluidStack {
			val (drained, additional) = this.drainDecimal(
				count.toBigDecimal(),
				action == IFluidHandler.FluidAction.SIMULATE
			)
			return FluidStack(
				additional[0] as Fluid,
				drained.capInt()
			)
		}

		override fun drain(
			stack: FluidStack,
			action: IFluidHandler.FluidAction
		): FluidStack = FluidStack(
			stack.fluid,
			this.drainDecimal(
				stack.amount.toBigDecimal(),
				action == IFluidHandler.FluidAction.SIMULATE,
				mutableListOf(stack.fluid)
			).first.capInt()
		)

		override fun serializeNBT(registries: HolderLookup.Provider): CompoundTag =
			super.serializeNBT(registries).also {
				it.putString("fluid", this.fluidType.descriptionId)
			}

		override fun deserializeNBT(registries: HolderLookup.Provider, tag: CompoundTag): Unit =
			super.deserializeNBT(registries, tag).also {
				this.fluid = BuiltInRegistries.FLUID.first { it.fluidType.descriptionId == tag.getString("fluid") }
			}
	}

	override fun fill(
		stack: FluidStack,
		action: IFluidHandler.FluidAction
	): Int = this.fillDecimal(
		stack.amount.toBigDecimal(),
		action == IFluidHandler.FluidAction.SIMULATE,
		mutableListOf(stack.fluid)
	).first.capInt()

	private var subExtractAction: ListenerHandler = { _, _, _, _ -> null }
	override var extractAction: ListenerHandler
		get() = { c, s, u, a ->
			var fluid = a.getOrNull(0) as? Fluid
			val unit = this.units[u]
			if (fluid == null) {
				a.add(0, unit.fluid)
				this.subExtractAction(c, s, u, a)
			} else {
				if (fluid == unit.fluid) this.subExtractAction(c, s, u, a)
				else BigDecimal.ZERO
			}
		}
		set(value) {
			this.subExtractAction = value
		}

	override fun drain(
		stack: FluidStack,
		action: IFluidHandler.FluidAction
	): FluidStack = FluidStack(
		stack.fluid,
		this.drainDecimal(
			stack.amount.toBigDecimal(),
			action == IFluidHandler.FluidAction.SIMULATE,
			mutableListOf(stack.fluid)
		).first.capInt()
	)

	override fun drain(
		count: Int,
		action: IFluidHandler.FluidAction
	): FluidStack {
		val (drained, additional) = this.drainDecimal(
			count.toBigDecimal(),
			action == IFluidHandler.FluidAction.SIMULATE
		)
		return FluidStack(
			additional[0] as Fluid,
			drained.capInt()
		)
	}

	override fun getTanks(): Int = this.units.size
	override fun getFluidInTank(tank: Int): FluidStack = this.units[tank].getFluid()
	override fun getTankCapacity(tank: Int): Int = this.units[tank].getCapacity()
	override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = this.units[tank].isFluidValid(stack)
	override fun getContainer(): ItemStack = this.itemContainer
}