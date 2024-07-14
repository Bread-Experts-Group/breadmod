package breadmod.util.capability

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import kotlin.math.min

/**
 * Item handler which can be used transparently as a [MutableList], as well as an [IItemHandler].
 *
 * @param slots A map of slots, determining their maximum size and [StorageDirection].
 * @param changed Lambda to execute whenever contents have been changed.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
open class IndexableItemHandler(private val slots: List<Pair<Int, StorageDirection>>, override var changed: (() -> Unit)? = null) : IItemHandler, ICapabilitySavable<CompoundTag>, MutableList<ItemStack> {
    private val stacks = MutableList(slots.size) { ItemStack.EMPTY }
    override val size: Int = slots.size

    override operator fun set(index: Int, element: ItemStack): ItemStack = stacks[index].also { changed?.invoke(); stacks[index] = element }
    override operator fun get(index: Int): ItemStack = stacks[index]

    override fun contains(element: ItemStack): Boolean = stacks.contains(element)

    override fun add(element: ItemStack): Boolean = stacks.add(element).also { changed?.invoke() }
    override fun add(index: Int, element: ItemStack) = stacks.add(index, element).also { changed?.invoke() }
    override fun addAll(index: Int, elements: Collection<ItemStack>): Boolean = stacks.addAll(index, elements).also { if(it) changed?.invoke() }
    override fun addAll(elements: Collection<ItemStack>): Boolean = stacks.addAll(elements).also { if(it) changed?.invoke() }

    override fun clear() = stacks.clear().also { changed?.invoke() }
    override fun containsAll(elements: Collection<ItemStack>): Boolean = stacks.containsAll(elements)
    override fun isEmpty(): Boolean = stacks.isEmpty()

    /**
     * [MutableIterator.next] will return a **COPY** of the [ItemStack] iterated over.
     *
     * [MutableIterator.remove] will remove the specified stack from the list, as well as notifying [changed].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun iterator(): MutableIterator<ItemStack> = object : MutableIterator<ItemStack> {
        private val base = stacks.iterator()
        override fun hasNext(): Boolean = base.hasNext()
        override fun next(): ItemStack = base.next().copy()
        override fun remove() = base.remove().also { changed?.invoke() }
    }
    /**
     * List mutating methods such as [MutableListIterator.add], [MutableListIterator.remove] and [MutableListIterator.set] will notify the attached
     * [changed] lambda. Methods such as [MutableListIterator.next] or [MutableListIterator.previous] will return a **COPY** of the requisite [ItemStack].
     * @see iterator
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun listIterator(): MutableListIterator<ItemStack> = object : MutableListIterator<ItemStack> {
        private val base = stacks.listIterator()
        override fun add(element: ItemStack) = base.add(element).also { changed?.invoke() }

        override fun hasNext(): Boolean = base.hasNext()
        override fun hasPrevious(): Boolean = base.hasPrevious()
        override fun next(): ItemStack = base.next().copy()
        override fun nextIndex(): Int = base.nextIndex()

        override fun previous(): ItemStack = base.previous().copy()
        override fun previousIndex(): Int = base.previousIndex()

        override fun remove() = base.remove().also { changed?.invoke() }
        override fun set(element: ItemStack) = base.set(element).also { changed?.invoke() }
    }
    override fun listIterator(index: Int): MutableListIterator<ItemStack> = stacks.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<ItemStack> = stacks.subList(fromIndex, toIndex)
    override fun retainAll(elements: Collection<ItemStack>): Boolean = stacks.retainAll(elements.toSet()).also { if(it) changed?.invoke() }

    override fun removeAll(elements: Collection<ItemStack>): Boolean = stacks.removeAll(elements.toSet()).also { if(it) changed?.invoke() }
    override fun removeAt(index: Int): ItemStack = stacks.removeAt(index).also { if(!it.isEmpty) changed?.invoke() }
    override fun remove(element: ItemStack): Boolean = stacks.remove(element).also { if(it) changed?.invoke() }

    override fun lastIndexOf(element: ItemStack): Int = stacks.lastIndexOf(element)
    override fun indexOf(element: ItemStack): Int = stacks.indexOf(element)
    
    override fun getSlots(): Int = size
    override fun getStackInSlot(slot: Int): ItemStack = stacks[slot].copy()

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = stack.copy().also {
        val reifiedSlot = slots[slot]
        val reifiedStack = stacks[slot]
        val toMove = min(min(reifiedSlot.first, reifiedStack.maxStackSize) - reifiedStack.count, it.count)

        if(reifiedSlot.second != StorageDirection.EMPTY_ONLY
            && toMove > 0
            && (reifiedStack.isEmpty || stack.item == reifiedStack.item)
        ) {
            if(!simulate) {
                if(reifiedStack.isEmpty) stacks[slot] = it.copyWithCount(toMove)
                else stacks[slot].grow(toMove)
            }
            it.shrink(toMove)
        } else return it
    }

    /**
     * Non-specific slot version of [insertItem]. Spreads the specified [stack] over all available slots.
     * This method will not modify the provided [stack] in correspondence with [insertItem]. It will return a copy of the remainder.
     * @see insertItem
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun insertItem(stack: ItemStack, simulate: Boolean): ItemStack = TODO("insert items 2")

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = TODO("extr items")

    /**
     * Non-specific slot version of [extractItem].
     * @see extractItem
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused", "ReplaceNotNullAssertionWithElvisReturn")
    fun extractItem(target: Item? = null, amount: Int, simulate: Boolean): ItemStack = TODO("extr items 2")

    override fun getSlotLimit(slot: Int): Int {
        val slotMax = slots[slot].first
        val slotActual = stacks[slot]
        return if(!slotActual.isEmpty) min(slotMax, slotActual.maxStackSize)
        else slotMax
    }
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = true

    override fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
        stacks.forEachIndexed { index, itemStack -> tag.put(index.toString(), itemStack.serializeNBT()) }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        nbt.allKeys.forEach { stacks[it.toInt()] = ItemStack.of(nbt.getCompound(it)) }
    }
}