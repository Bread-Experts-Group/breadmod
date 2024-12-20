package org.bread_experts_group.breadmod.experimental.recipe

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import org.bread_experts_group.breadmod.registry.menu.actual.AbstractModContainerMenu

abstract class AbstractTestRecipeMenu(
	menuType : MenuType<*>,
	containerId : Int,
	inventory : Inventory,
	val parent : AbstractTestRecipeBlockEntity<*, *>,
) : AbstractModContainerMenu(menuType, containerId) {
	init {
		this.addInventorySlots(inventory, 8, 174, 116)
	}
}