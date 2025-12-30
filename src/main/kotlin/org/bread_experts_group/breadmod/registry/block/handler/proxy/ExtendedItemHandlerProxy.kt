package org.bread_experts_group.breadmod.registry.block.handler.proxy

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler

/**
 * Proxy handler of [ExtendedItemHandler].
 */
class ExtendedItemHandlerProxy(
	parent: ExtendedItemHandler,
	vararg slotMap: Pair<Int, Int>
) : HandlerProxy<ExtendedItemHandler>(parent, *slotMap), IItemHandler {
	override fun getSlots(): Int = this.map.size

	override fun getStackInSlot(slot: Int): ItemStack = this.parent.getStackInSlot(this.redirectSlot(slot))

	override fun insertItem(
		slot: Int,
		stack: ItemStack,
		simulate: Boolean
	): ItemStack = this.parent.insertItem(this.redirectSlot(slot), stack, simulate)

	override fun extractItem(
		slot: Int,
		amount: Int,
		simulate: Boolean
	): ItemStack = this.parent.extractItem(this.redirectSlot(slot), amount, simulate)

	override fun getSlotLimit(slot: Int): Int =
		this.parent.getSlotLimit(this.redirectSlot(slot))

	override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
		this.parent.isItemValid(this.redirectSlot(slot), stack)
}