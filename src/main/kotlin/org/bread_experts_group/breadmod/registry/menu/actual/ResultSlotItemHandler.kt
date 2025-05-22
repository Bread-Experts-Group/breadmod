package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

class ResultSlotItemHandler(
	itemHandler: ExpansibleItemHandler,
	index: Int,
	xPosition: Int,
	yPosition: Int
) : ModifiedSlotItemHandler(itemHandler, index, xPosition, yPosition) {
	override fun mayPlace(stack: ItemStack): Boolean = false
}