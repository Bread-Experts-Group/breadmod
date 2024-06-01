package breadmod.util

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.ItemStackHandler

open class IndexableItemHandler(override val size: Int, val changed: () -> Unit) : ItemStackHandler(size), MutableList<ItemStack> {
    val items: NonNullList<ItemStack> = this.stacks

    override operator fun set(index: Int, element: ItemStack): ItemStack = items[index].also { changed(); items[index] = element }
    override operator fun get(index: Int) = items[index]

    override fun contains(element: ItemStack): Boolean = this.stacks.contains(element)

    override fun add(element: ItemStack): Boolean = this.stacks.add(element)
    override fun add(index: Int, element: ItemStack) = this.stacks.add(index, element)
    override fun addAll(index: Int, elements: Collection<ItemStack>): Boolean = this.addAll(index, elements)
    override fun addAll(elements: Collection<ItemStack>): Boolean = this.addAll(elements)

    override fun clear() = this.stacks.clear()
    override fun containsAll(elements: Collection<ItemStack>): Boolean = this.stacks.containsAll(elements)
    override fun isEmpty(): Boolean = this.stacks.isEmpty()

    override fun iterator(): MutableIterator<ItemStack> = this.stacks.iterator()
    override fun listIterator(): MutableListIterator<ItemStack> = this.stacks.listIterator()
    override fun listIterator(index: Int): MutableListIterator<ItemStack> = this.stacks.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<ItemStack> = this.stacks.subList(fromIndex, toIndex)
    override fun retainAll(elements: Collection<ItemStack>): Boolean = this.stacks.retainAll(elements.toSet())

    override fun removeAll(elements: Collection<ItemStack>): Boolean = this.stacks.removeAll(elements.toSet())
    override fun removeAt(index: Int): ItemStack = this.stacks.removeAt(index)
    override fun remove(element: ItemStack): Boolean = this.stacks.remove(element)

    override fun lastIndexOf(element: ItemStack): Int = this.stacks.lastIndexOf(element)
    override fun indexOf(element: ItemStack): Int = this.stacks.indexOf(element)
}