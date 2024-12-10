package org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid_item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractRecipeScreen

class SingleFluidItemScreen(
    menu: SingleFluidItemRecipeMenu,
    inventory: Inventory,
    title: Component
) : AbstractRecipeScreen<SingleFluidItemRecipeMenu>(menu, inventory, title)