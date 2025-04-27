package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.util.capInt
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler.ExpansibleSlot
import java.math.BigDecimal
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrElse
import kotlin.math.min

@Suppress("ConvertLambdaToReference")
class ExpansibleItemHandler(
	override val units: MutableList<ExpansibleSlot>
) : AbstractExpansibleHandler<ExpansibleSlot>(), IItemHandler, IItemHandlerModifiable {
	constructor(slots: Int) : this(MutableList(slots) { ExpansibleSlot() })

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
		var item: Item = Items.AIR,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : HandlerSerializable {
		override var capacity: BigDecimal? = BigDecimal(this.asStack.maxStackSize)
			set(value) {
				field = if (value != null && value <= BigDecimal.ZERO) null else value
			}
		val isEmpty: Boolean
			get() = this.item == Items.AIR || this.amount == BigDecimal.ZERO
		var asStack: ItemStack
			get() = ItemStack(this.item, min(this.amount.capInt(), 99))/*.also {
				it.update(ModDataComponents.EXPANSIBLE_ITEM_STACK, BigDecimal.ZERO) { this.amount }
			}*/
			set(value) {
				this.item = value.item
				this.amount = value.get(ModDataComponents.EXPANSIBLE_ITEM_STACK) ?: value.count.toBigDecimal()
			}

		override fun serializeNBT(registries: HolderLookup.Provider): CompoundTag =
			super.serializeNBT(registries).also {
				if (this.asStack.isEmpty) return@also
				it.put("item", this.asStack.save(registries))
			}

		override fun deserializeNBT(registries: HolderLookup.Provider, tag: CompoundTag) {
			this.item = ItemStack.parse(registries, tag.get("item") ?: return).getOrElse(ItemStack::EMPTY).item
		}
	}

	val isEmpty: Boolean
		get() = this.units.all { it.isEmpty }
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
			if (stack.count + target.asStack.count > target.asStack.maxStackSize) return ItemStack.EMPTY
			val moved = target.fillDecimal(
				stack.count.toBigDecimal(),
				false,
				mutableListOf(stack.item)
			).first.capInt()
			stack.copy().also {
				if (target.isEmpty) target.asStack = it else target.asStack.grow(moved)
			}
		} else ItemStack.EMPTY
	}

	override fun extractItem(slot: Int, count: Int, simulate: Boolean): ItemStack {
		val target = this.units[slot]
		return if (target.maxOut != null) {
			val (bCount, _) = target.drainDecimal(count.toBigDecimal(), false)
			ItemStack(target.item, bCount.capInt())
		} else ItemStack.EMPTY
	}

	override fun getSlots(): Int = this.units.size
	override fun getStackInSlot(slot: Int): ItemStack = this.units[slot].asStack
	override fun getSlotLimit(slot: Int): Int =
		this.units[slot].capacity?.capInt() ?: this.units[slot].asStack.maxStackSize

	override fun isItemValid(slot: Int, stack: ItemStack): Boolean = this.units[slot].filter.test(stack)
	override fun setStackInSlot(slot: Int, stack: ItemStack) {
		this.units[slot].asStack = stack
	}
}