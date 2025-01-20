package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid_item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidItemRecipe

class SingleFluidItemScreen(
	menu: SingleFluidItemRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<
		BMRecipeInputs.SingleFluidItem,
		SingleFluidItemRecipe,
		SingleFluidItemRecipeBlockEntity,
		SingleFluidItemRecipeMenu>(
	menu,
	inventory,
	title
)