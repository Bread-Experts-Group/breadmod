package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen

class SingleFluidScreen(
	menu : SingleFluidRecipeMenu,
	inventory : Inventory,
	title : Component
) : AbstractRecipeScreen<SingleFluidRecipeMenu>(menu, inventory, title)