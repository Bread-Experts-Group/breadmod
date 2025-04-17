package org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiFluidTestRecipe

class MultiFluidScreen(
	menu: MultiFluidRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<
		BMRecipeInputs.MultiFluid,
		MultiFluidTestRecipe,
		MultiFluidRecipeBlockEntity,
		MultiFluidRecipeMenu>(
	menu,
	inventory,
	title
)