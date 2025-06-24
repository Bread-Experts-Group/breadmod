package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandlerModifiable
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.util.capInt
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler.ExpansibleSlot
import java.math.BigDecimal
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrElse
import kotlin.math.min

// todo redo to account for sidedness, maybe a proxy system like what mekanism does
@Suppress("ConvertLambdaToReference")
open class ExpansibleItemHandler(
	override val units: MutableList<ExpansibleSlot>
) : AbstractExpansibleHandler<ExpansibleSlot>(), IItemHandlerModifiable {
	constructor(slots: Int) : this(MutableList(slots) { ExpansibleSlot() })

	open fun onContentsChanged(slot: Int) {}

	private var allowedSides: List<Direction?> = listOf(null)
	fun getThisForSide(direction: Direction?): ExpansibleItemHandler? =
		if (this.allowedSides.contains(direction) || this.allowedSides.contains(null)) this else null

	fun setMaxIn(slot: Int, maxIn: Int) {
		val amount = if (maxIn == 0) null else BigDecimal(maxIn)
		this.units[slot].maxIn = amount
	}

	fun setMaxOut(slot: Int, maxOut: Int) {
		val amount = if (maxOut == 0) null else BigDecimal(maxOut)
		this.units[slot].maxOut = amount
	}

	fun setMaxInOut(slot: Int, maxIn: Int, maxOut: Int) {
		this.setMaxIn(slot, maxIn)
		this.setMaxOut(slot, maxOut)
	}

	fun setAllowedSides(vararg direction: Direction?) {
		this.allowedSides = direction.toList()
	}

	class ExpansibleSlot(
		override var maxIn: BigDecimal? = BigDecimal(64),
		override var maxOut: BigDecimal? = BigDecimal(64),
		var filter: Predicate<ItemStack> = Predicate { _ -> true },
		var stack: ItemStack = ItemStack.EMPTY,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : HandlerSerializable {
		override var capacity: BigDecimal? = BigDecimal(this.stack.maxStackSize)
			set(value) {
				field = if (value != null && value <= BigDecimal.ZERO) null else value
			}
		val isEmpty: Boolean
			get() = this.stack.isEmpty && this.amount == BigDecimal.ZERO

		override fun serializeNBT(registries: HolderLookup.Provider): CompoundTag =
			super.serializeNBT(registries).also {
				if (this.stack.isEmpty) return@also
				it.put("item", this.stack.save(registries))
			}

		override fun deserializeNBT(registries: HolderLookup.Provider, tag: CompoundTag) {
			this.stack = ItemStack.parse(registries, tag.get("item") ?: return).getOrElse(ItemStack::EMPTY)
		}
	}

	override fun isEmpty(): Boolean = this.units.all { it.isEmpty }
	val filledSlots: Int
		get() = this.units.count { !it.isEmpty }

	//	val emptySlots: Int
//		get() = this.units.count { it.isEmpty }
	override fun insertItem(
		slot: Int,
		stack: ItemStack,
		simulate: Boolean
	): ItemStack {
		val target = this.units[slot]
		return if (target.maxIn != null && !simulate && this.isItemValid(slot, stack)) {
			if (stack.count + target.stack.count > target.stack.maxStackSize) return ItemStack.EMPTY
			val moved = target.fillDecimal(
				stack.count.toBigDecimal(),
				false,
				mutableListOf(stack.item)
			).first.capInt()
			this.onContentsChanged(slot)
			stack.also {
				if (target.isEmpty) target.stack = it.copy() else target.stack.grow(moved)
			}
		} else ItemStack.EMPTY
	}

	// todo fix logic
	override fun extractItem(slot: Int, count: Int, simulate: Boolean): ItemStack {
		val target = this.units[slot]
//		return target.stack.copy()
		LogManager.getLogger().info(this.amount)
		if (this.amount == BigDecimal.ZERO) return ItemStack.EMPTY
		return if (target.maxOut != null) {
			val bCount = target.drainDecimal(count.toBigDecimal(), false).first.toInt()
			this.onContentsChanged(slot)
			target.stack.copyWithCount(bCount)
		} else ItemStack.EMPTY
	}

	fun extractItemInternal(slot: Int, count: Int, simulate: Boolean): ItemStack {
		if (count == 0) return ItemStack.EMPTY
		val unit = this.units[slot]
		val existing = unit.stack

		if (existing.isEmpty) return ItemStack.EMPTY
		val toExtract = min(existing.count, count)

		if (existing.count <= toExtract) {
			if (!simulate) {
				this.setStackInSlot(slot, ItemStack.EMPTY)
				this.onContentsChanged(slot)
				return existing
			}
			return existing.copy()
		}
		if (!simulate) {
			this.setStackInSlot(slot, existing.copyWithCount(existing.count - toExtract))
			this.onContentsChanged(slot)
		}
		return existing.copyWithCount(toExtract)
	}

	override fun getSlots(): Int = this.units.size
	override fun getStackInSlot(slot: Int): ItemStack = this.units[slot].stack
	override fun getSlotLimit(slot: Int): Int =
		this.units[slot].capacity?.capInt() ?: this.units[slot].stack.maxStackSize

	override fun isItemValid(slot: Int, stack: ItemStack): Boolean = this.units[slot].filter.test(stack)
	override fun setStackInSlot(slot: Int, stack: ItemStack) {
		this.units[slot].stack = stack
	}
}