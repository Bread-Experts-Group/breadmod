package org.bread_experts_group.breadmod.util.handlers

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

	class ExpansibleSlot(
		capacity: BigDecimal? = null,
		override var maxIn: BigDecimal? = null,
		override var maxOut: BigDecimal? = null,
		var filter: Predicate<ItemStack> = Predicate { _ -> true },
		var item: Item = Items.AIR,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : HandlerSerializable {
		override var capacity: BigDecimal? = capacity
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
		val moved = this.units[slot].fillDecimal(
			stack.count.toBigDecimal(),
			simulate,
			mutableListOf(stack.item)
		).first.capInt()
		return stack.copy().also { it.shrink(moved) }
	}

	override fun extractItem(slot: Int, count: Int, simulate: Boolean): ItemStack {
		val unit = this.units[slot]
		val (count, _) = unit.drainDecimal(
			count.toBigDecimal(),
			simulate
		)
		return ItemStack(
			unit.item,
			count.capInt()
		)
	}

	override fun getSlots(): Int = this.units.size
	override fun getStackInSlot(slot: Int): ItemStack = this.units[slot].asStack
	override fun getSlotLimit(slot: Int): Int = this.units[slot].capacity?.capInt() ?: 99
	override fun isItemValid(slot: Int, stack: ItemStack): Boolean = this.units[slot].filter.test(stack)
	override fun setStackInSlot(slot: Int, stack: ItemStack) {
		this.units[slot].asStack = stack
	}
}