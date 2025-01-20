package org.bread_experts_group.breadmod.experimental.recipe

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import org.bread_experts_group.breadmod.registry.menu.actual.AbstractModContainerMenu

abstract class AbstractTestRecipeMenu<
		INPUT : RecipeInput,
		RECIPE : Recipe<INPUT>,
		T : AbstractTestRecipeBlockEntity<INPUT, RECIPE, T>,
		M : AbstractTestRecipeMenu<INPUT, RECIPE, T, M>
		>(
	menuType: MenuType<M>,
	containerId: Int,
	inventory: Inventory,
	val parent: T,
) : AbstractModContainerMenu<T>(menuType, containerId, parent) {
	init {
		this.addInventorySlots(inventory, 8, 174, 116)
	}
}