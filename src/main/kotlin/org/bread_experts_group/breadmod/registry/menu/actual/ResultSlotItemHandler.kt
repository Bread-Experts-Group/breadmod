package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler

class ResultSlotItemHandler(
	itemHandler : IItemHandler,
	index : Int,
	xPosition : Int,
	yPosition : Int
) : SlotItemHandler(itemHandler, index, xPosition, yPosition) {
	override fun mayPlace(stack : ItemStack) : Boolean = false
}