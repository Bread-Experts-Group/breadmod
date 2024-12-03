package org.bread_experts_group.breadmod.experimental.block.single_fluid

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.AbstractRecipeScreen

class SingleFluidScreen(
    menu: SingleFluidRecipeMenu,
    inventory: Inventory,
    title: Component
) : AbstractRecipeScreen<SingleFluidRecipeMenu>(menu, inventory, title)