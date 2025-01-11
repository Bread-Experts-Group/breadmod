package org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen

class MultiFluidScreen(
	menu: MultiFluidRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<MultiFluidRecipeMenu>(menu, inventory, title)