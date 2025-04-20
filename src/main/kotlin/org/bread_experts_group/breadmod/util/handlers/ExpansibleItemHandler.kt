package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.WorldlyContainer
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

// todo figure out sidedness later i guess..
//  rewrite maybe???
@Suppress("ConvertLambdaToReference")
class ExpansibleItemHandler(
	override val units: MutableList<ExpansibleSlot>,
	private val inv: WorldlyContainer
) : AbstractExpansibleHandler<ExpansibleSlot>(), IItemHandler, IItemHandlerModifiable {
	constructor(slots: Int, inv: WorldlyContainer) : this(MutableList(slots) { ExpansibleSlot() }, inv)

	class ExpansibleSlot(
		capacity: BigDecimal? = null,
		override var maxIn: BigDecimal? = null,
		override var maxOut: BigDecimal? = null,
		var filter: Predicate<ItemStack> = Predicate { _ -> true },
		var item: Item = Items.AIR,
		override var amount: BigDecimal = BigDecimal.ZERO,
		var sides: List<Direction> = Companion.ALL_SIDES
	) : HandlerSerializable {
		companion object {
			val ALL_SIDES: List<Direction> = listOf(NORTH, SOUTH, EAST, WEST, UP, DOWN)
		}

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
		val sides = this.units[slot].sides
		// todo this locks up the game somehow
//		if (sides.isNotEmpty() && !sides.any { !this.inv.canPlaceItemThroughFace(slot, stack, it) }) return stack
		val moved = this.units[slot].fillDecimal(
			stack.count.toBigDecimal(),
			simulate,
			mutableListOf(stack.item)
		).first.capInt()
		return stack.copy().also { it.shrink(moved) }
	}

	override fun extractItem(slot: Int, count: Int, simulate: Boolean): ItemStack {
		val unit = this.units[slot]
		// todo this locks up the game somehow
//		val sides = unit.sides
//		if (sides.isNotEmpty() && sides.any { !this.inv.canTakeItemThroughFace(slot, this.getStackInSlot(slot), it) })
//			return ItemStack.EMPTY

		val (count, _) = unit.drainDecimal(count.toBigDecimal(), simulate)
		return ItemStack(unit.item, count.capInt())
	}

	override fun getSlots(): Int = this.units.size
	override fun getStackInSlot(slot: Int): ItemStack = this.units[slot].asStack
	override fun getSlotLimit(slot: Int): Int = this.units[slot].capacity?.capInt() ?: 99
	override fun isItemValid(slot: Int, stack: ItemStack): Boolean = this.units[slot].filter.test(stack)
	override fun setStackInSlot(slot: Int, stack: ItemStack) {
		this.units[slot].asStack = stack
	}
}