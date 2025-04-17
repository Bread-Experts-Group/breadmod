package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe

class SingleFluidScreen(
	menu: SingleFluidRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<
		BMRecipeInputs.SingleFluid,
		SingleFluidTestRecipe,
		SingleFluidRecipeBlockEntity,
		SingleFluidRecipeMenu>(
	menu,
	inventory,
	title
)