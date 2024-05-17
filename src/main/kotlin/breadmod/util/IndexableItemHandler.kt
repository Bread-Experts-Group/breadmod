package breadmod.util

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.ItemStackHandler

class IndexableItemHandler(size: Int) : ItemStackHandler(size) {
    val exposed: NonNullList<ItemStack> = this.stacks
    operator fun set(index: Int, value: ItemStack) { exposed[index] = value }
    operator fun get(index: Int) = exposed[index]
}