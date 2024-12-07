package org.bread_experts_group.breadmod.experimental.recipe_related.block.single_item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractRecipeScreen

class SingleItemScreen(
    menu: SingleItemRecipeMenu,
    inventory: Inventory,
    title: Component
) : AbstractRecipeScreen<SingleItemRecipeMenu>(menu, inventory, title)