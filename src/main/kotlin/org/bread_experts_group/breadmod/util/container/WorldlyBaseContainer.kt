package org.bread_experts_group.breadmod.util.container

import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface WorldlyBaseContainer : WorldlyContainer {

    /**
     * The list that holds the container's items
     */
    var items: NonNullList<ItemStack>

    override fun clearContent() = items.clear()

    override fun getContainerSize(): Int = items.size

    override fun isEmpty(): Boolean = items.isEmpty()

    override fun getItem(slot: Int): ItemStack = items[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack =
        items[slot].split(amount) ?: ItemStack.EMPTY

    override fun removeItemNoUpdate(slot: Int): ItemStack =
        items[slot].copyAndClear() ?: ItemStack.EMPTY

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        setChanged()
    }

    override fun stillValid(player: Player): Boolean = true
}