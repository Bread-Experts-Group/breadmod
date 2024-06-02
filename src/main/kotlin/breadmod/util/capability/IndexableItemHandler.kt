package breadmod.util.capability

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.util.INBTSerializable
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
open class IndexableItemHandler(private val slots: List<Pair<Int, StorageDirection>>, val changed: () -> Unit) : IItemHandler, INBTSerializable<CompoundTag>, MutableList<ItemStack> {
    private val stacks = MutableList(slots.size) { ItemStack.EMPTY }
    override val size: Int = slots.size

    override operator fun set(index: Int, element: ItemStack): ItemStack = stacks[index].also { changed(); stacks[index] = element }
    override operator fun get(index: Int): ItemStack = stacks[index]

    override fun contains(element: ItemStack): Boolean = stacks.contains(element)

    override fun add(element: ItemStack): Boolean = stacks.add(element).also { changed() }
    override fun add(index: Int, element: ItemStack) = stacks.add(index, element).also { changed() }
    override fun addAll(index: Int, elements: Collection<ItemStack>): Boolean = stacks.addAll(index, elements).also { if(it) changed() }
    override fun addAll(elements: Collection<ItemStack>): Boolean = stacks.addAll(elements).also { if(it) changed() }

    override fun clear() = stacks.clear().also { changed() }
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
        override fun remove() = base.remove().also { changed() }
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
        override fun add(element: ItemStack) = base.add(element).also { changed() }

        override fun hasNext(): Boolean = base.hasNext()
        override fun hasPrevious(): Boolean = base.hasPrevious()
        override fun next(): ItemStack = base.next().copy()
        override fun nextIndex(): Int = base.nextIndex()

        override fun previous(): ItemStack = base.previous().copy()
        override fun previousIndex(): Int = base.previousIndex()

        override fun remove() = base.remove().also { changed() }
        override fun set(element: ItemStack) = base.set(element).also { changed }
    }
    override fun listIterator(index: Int): MutableListIterator<ItemStack> = stacks.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<ItemStack> = stacks.subList(fromIndex, toIndex)
    override fun retainAll(elements: Collection<ItemStack>): Boolean = stacks.retainAll(elements.toSet()).also { if(it) changed() }

    override fun removeAll(elements: Collection<ItemStack>): Boolean = stacks.removeAll(elements.toSet()).also { if(it) changed() }
    override fun removeAt(index: Int): ItemStack = stacks.removeAt(index).also { if(!it.isEmpty) changed() }
    override fun remove(element: ItemStack): Boolean = stacks.remove(element).also { if(it) changed() }

    override fun lastIndexOf(element: ItemStack): Int = stacks.lastIndexOf(element)
    override fun indexOf(element: ItemStack): Int = stacks.indexOf(element)
    
    override fun getSlots(): Int = size
    override fun getStackInSlot(slot: Int): ItemStack = stacks[slot].copy()

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = stack.copy().also {
        val reifiedSlot = slots[slot]
        if(reifiedSlot.second != StorageDirection.EMPTY_ONLY) {
            val space = min(min(stack.maxStackSize, reifiedSlot.first)-stack.count, it.count)
            it.shrink(space)
            if(!simulate) stacks[slot].grow(space)
            if(it.count == 0) return ItemStack.EMPTY
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
    fun insertItem(stack: ItemStack, simulate: Boolean): ItemStack = stack.copy().also {
        val toFill = slots
            .filterIndexed { index, (max, direction) -> max > 0 && stacks[index].let { actual -> actual.isEmpty ||( actual.`is`(it.item) && actual.count != actual.maxStackSize) } && direction != StorageDirection.EMPTY_ONLY }
            .mapIndexed { index, pair -> stacks[index] to (pair to index) }
            .sortedWith { (stack1, pair1), (stack2, pair2) ->
                val cmp = (stack1.maxStackSize-stack1.count).compareTo(stack2.maxStackSize-stack2.count)
                if(cmp != 1) pair1.first.first.compareTo(pair2.first.first) else cmp
            }
        toFill.forEach { (stack, pair) ->
            val space = min(min(stack.maxStackSize, pair.first.first)-stack.count, it.count)
            it.shrink(space)
            if(!simulate) {
                if(stack.isEmpty) stacks[pair.second] = it.copyWithCount(space)
                else stack.grow(space)
            }
            if(it.count == 0) return ItemStack.EMPTY
        }
        return it
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val reifiedSlot = slots[slot]
        if(reifiedSlot.second != StorageDirection.STORE_ONLY) {
            val stack = stacks[slot]
            val removed = stack.copyWithCount(min(amount, stack.count))
            if(!simulate) stack.shrink(removed.count)
            return removed
        } else return ItemStack.EMPTY
    }

    /**
     * Non-specific slot version of [extractItem].
     * @see extractItem
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused", "ReplaceNotNullAssertionWithElvisReturn")
    fun extractItem(target: Item? = null, amount: Int, simulate: Boolean): ItemStack {
        val reifiedTarget = target ?: (stacks.firstOrNull { !it.isEmpty } ?: return ItemStack.EMPTY).item
        var remainder = amount

        val toFill = slots
            .filterIndexed { index, (max, direction) -> max > 0 && stacks[index].let { actual -> !actual.isEmpty && actual.`is`(reifiedTarget) } && direction != StorageDirection.STORE_ONLY }
            .mapIndexed { index, pair -> stacks[index] to pair }
            .sortedWith { (stack1), (stack2) -> stack1.count.compareTo(stack2.count) }
        var concatStack: ItemStack? = null
        toFill.forEach { (stack) ->
            val toRemove = min(remainder, stack.count)
            if(concatStack == null) concatStack = stack.copyWithCount(toRemove) else concatStack!!.grow(toRemove)
            remainder -= concatStack!!.count
            if(!simulate) stack.shrink(toRemove)
        }
        return concatStack ?: ItemStack.EMPTY
    }

    override fun getSlotLimit(slot: Int): Int = min(slots[slot].first, stacks[slot].maxStackSize)
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = true

    override fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
        stacks.forEachIndexed { index, itemStack -> tag.put(index.toString(), itemStack.serializeNBT()) }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        nbt.allKeys.forEach { stacks[it.toInt()] = ItemStack.of(nbt.getCompound(it)) }
    }
}