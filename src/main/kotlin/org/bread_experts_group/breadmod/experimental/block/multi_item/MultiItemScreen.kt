package org.bread_experts_group.breadmod.experimental.block.multi_item

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.AbstractRecipeScreen

class MultiItemScreen(
    menu: MultiItemRecipeMenu,
    inventory: Inventory,
    title: Component
) : AbstractRecipeScreen<MultiItemRecipeMenu>(menu, inventory, title)