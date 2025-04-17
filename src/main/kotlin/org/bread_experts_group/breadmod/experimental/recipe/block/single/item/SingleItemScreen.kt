package org.bread_experts_group.breadmod.experimental.recipe.block.single.item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleItemTestRecipe

class SingleItemScreen(
	menu: SingleItemRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<
		BMRecipeInputs.SingleItem,
		SingleItemTestRecipe,
		SingleItemRecipeBlockEntity,
		SingleItemRecipeMenu>(
	menu,
	inventory,
	title
)