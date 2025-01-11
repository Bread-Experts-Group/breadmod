package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class ResultSlot(id: Int, x: Int, y: Int, container: Container) : Slot(container, id, x, y) {
	override fun mayPlace(stack: ItemStack): Boolean = false
}