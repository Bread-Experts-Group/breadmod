package org.bread_experts_group.breadmod.util.container

import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandlerModifiable
import kotlin.math.min

// todo make a sided version of this
// todo bigger issue, evaluate and see if we even need this class to begin with
/**
 * Creates an empty container with a specified [size].
 */
open class ItemHandlerContainer(
    private val size: Int
) : IItemHandlerModifiable, Container {
    var items: NonNullList<ItemStack> = NonNullList.withSize(size, ItemStack.EMPTY)

    override fun getSlots(): Int = containerSize

    override fun getStackInSlot(slot: Int): ItemStack = getItem(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty) return ItemStack.EMPTY
        val stackInSlot: ItemStack = this.getItem(slot)

        if (!stackInSlot.isEmpty) {
            if (stackInSlot.count >= min(
                    stackInSlot.maxStackSize.toDouble(),
                    getSlotLimit(slot).toDouble()
                )
            ) return stack

            if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) return stack
            if (!this.canPlaceItem(slot, stack)) return stack
            val m = (min(stack.maxStackSize.toDouble(), getSlotLimit(slot).toDouble()) - stackInSlot.count).toInt()

            if (stack.count <= m) {
                if (!simulate) {
                    val copy = stack.copy()
                    copy.grow(stackInSlot.count)
                    this.setItem(slot, copy)
                    this.setChanged()
                }

                return ItemStack.EMPTY
            } else {
                // copy the stack to not modify the original one
                val newStack = stack.copy()
                if (!simulate) {
                    val copy = newStack.split(m)
                    copy.grow(stackInSlot.count)
                    this.setItem(slot, copy)
                    this.setChanged()
                    return newStack
                } else {
                    newStack.shrink(m)
                    return newStack
                }
            }
        } else {
            if (!this.canPlaceItem(slot, stack)) return stack

            val m = min(stack.maxStackSize.toDouble(), getSlotLimit(slot).toDouble()).toInt()
            if (m < stack.count) {
                // copy the stack to not modify the original one
                val newStack = stack.copy()
                if (!simulate) {
                    this.setItem(slot, newStack.split(m))
                    this.setChanged()
                    return newStack
                } else {
                    newStack.shrink(m)
                    return newStack
                }
            } else {
                if (!simulate) {
                    this.setItem(slot, stack)
                    this.setChanged()
                }
                return ItemStack.EMPTY
            }
        }
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (amount == 0) return ItemStack.EMPTY
        val stackInSlot: ItemStack = this.getItem(slot)
        if (stackInSlot.isEmpty) return ItemStack.EMPTY

        if (simulate) {
            if (stackInSlot.count < amount) {
                return stackInSlot.copy()
            } else {
                val copy = stackInSlot.copy()
                copy.count = amount
                return copy
            }
        } else {
            val m = min(stackInSlot.count.toDouble(), amount.toDouble()).toInt()

            val decrementStackSize: ItemStack = this.removeItem(slot, m)
            this.setChanged()
            return decrementStackSize
        }
    }

    override fun getSlotLimit(slot: Int): Int = this.maxStackSize

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = this.canPlaceItem(slot, stack)

    override fun setStackInSlot(slot: Int, stack: ItemStack) = this.setItem(slot, stack)

    override fun clearContent() = items.forEach { it.count = 0 }
    override fun getContainerSize(): Int = items.size
    override fun isEmpty(): Boolean = items.isEmpty()
    override fun getItem(slot: Int): ItemStack = items[slot]
    override fun removeItem(slot: Int, amount: Int): ItemStack = items[slot].split(amount)
    override fun removeItemNoUpdate(slot: Int): ItemStack = items[slot].copyAndClear()

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
    }

    override fun setChanged() {}

    override fun stillValid(player: Player): Boolean = true
}