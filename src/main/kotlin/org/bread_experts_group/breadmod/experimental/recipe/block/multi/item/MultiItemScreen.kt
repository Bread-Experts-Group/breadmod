package org.bread_experts_group.breadmod.experimental.recipe.block.multi.item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen

class MultiItemScreen(
	menu: MultiItemRecipeMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeScreen<MultiItemRecipeMenu>(menu, inventory, title)