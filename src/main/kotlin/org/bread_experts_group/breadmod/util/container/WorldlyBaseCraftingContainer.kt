package org.bread_experts_group.breadmod.util.container

import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack

interface WorldlyBaseCraftingContainer : WorldlyBaseContainer, CraftingContainer {
    override fun getItems(): MutableList<ItemStack> = items

    override fun fillStackedContents(contents: StackedContents) {
        for (stack: ItemStack in items) {
            contents.accountSimpleStack(stack)
        }
    }
}