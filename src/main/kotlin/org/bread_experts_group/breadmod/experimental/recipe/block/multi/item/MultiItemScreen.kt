package org.bread_experts_group.breadmod.experimental.recipe.block.multi.item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiItemTestRecipe

class MultiItemScreen(
	menu: MultiItemRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<
		BMRecipeInputs.MultiItem,
		MultiItemTestRecipe,
		MultiItemRecipeBlockEntity,
		MultiItemRecipeMenu>(
	menu,
	inventory,
	title
)