package breadmod.util.capability

import breadmod.util.ObservableList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import kotlin.math.min

/**
 * Item handler which can be used transparently as a [MutableList], as well as an [IItemHandler].
 *
 * @param slots A map of slots, determining their maximum size and [StorageDirection].
 * @param slotChanged Callback to execute whenever contents have been changed.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
open class IndexableItemHandler(
    private val slots: List<Pair<Int, StorageDirection>>,
    var slotChanged: ((index: Int, stack: ItemStack) -> Unit)? = null
) : IItemHandler, ICapabilitySavable<CompoundTag>,
    ObservableList<ItemStack>(slots.size, { ItemStack.EMPTY }, slotChanged) {
    override var changed: (() -> Unit)? = { slotChanged?.invoke(0, ItemStack.EMPTY) }

    /**
     * Retrieves the number of slots in this [IndexableItemHandler].
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun getSlots(): Int = size


    /**
     * Retrieves the [ItemStack] in the specified [slot].
     *
     * **NOTE**: This method returns a copy of the [ItemStack].
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun getStackInSlot(slot: Int): ItemStack = this[slot].copy()

    /**
     * Conditional lambda to check if an item can be inserted into a slot.
     *
     * @see extractItemCheck
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var insertItemCheck: ((slot: Int, stack: ItemStack, simulate: Boolean) -> Boolean)? = { _, _, _ -> true }

    /**
     * Conditional lambda to check if an item can be extracted from a slot.
     *
     * @see extractItemCheck
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var extractItemCheck: ((slot: Int, amount: Int, simulate: Boolean) -> Boolean)? = { _, _, _ -> true }

    /**
     * Inserts an item into the specified [slot].
     *
     * @see insertItemCheck
     * @see extractItem
     * @author Maiko Elbrecht
     * @since 1.0.0
     */
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = stack.copy().also {
        if (insertItemCheck?.invoke(slot, stack, simulate) != true) return it
        val reifiedSlot = slots[slot]
        val reifiedStack = this[slot]
        val toMove = min(min(reifiedSlot.first, reifiedStack.maxStackSize) - reifiedStack.count, it.count)

        if (reifiedSlot.second != StorageDirection.EMPTY_ONLY
            && toMove > 0
            && (reifiedStack.isEmpty || stack.item == reifiedStack.item)
        ) {
            if (!simulate) {
                if (reifiedStack.isEmpty) this[slot] = it.copyWithCount(toMove)
                else this[slot].grow(toMove)
            }
            it.shrink(toMove)
        } else return it
    }

    /**
     * Non-specific slot version of [insertItem].
     * Spreads the specified [stack] over all slots.
     * This method will not modify the provided [stack] in correspondence with [insertItem].
     * It will return a copy of the remainder.
     * @see insertItem
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused", "UNUSED_PARAMETER")
    fun insertItem(stack: ItemStack, simulate: Boolean): ItemStack = TODO("insert items 2")

    /**
     * Extracts an item from the specified [slot].
     *
     * @see extractItemCheck
     * @see insertItem
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (extractItemCheck?.invoke(slot, amount, simulate) != true) return ItemStack.EMPTY
        val reifiedSlot = slots[slot]
        val reifiedStack = this[slot]
        val toMove = min(reifiedStack.count, amount)

        return if (reifiedSlot.second != StorageDirection.STORE_ONLY && toMove > 0) {
            val extracted = reifiedStack.copy().also { it.count = toMove }
            if (!simulate) reifiedStack.shrink(toMove)
            extracted
        } else ItemStack.EMPTY
    }

    /**
     * Non-specific slot version of [extractItem].
     * @see extractItem
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused", "UNUSED_PARAMETER")
    fun extractItem(target: Item? = null, amount: Int, simulate: Boolean): ItemStack = TODO("extract items 2")


    /**
     * Retrieves the maximum stack size for the specified [slot].
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun getSlotLimit(slot: Int): Int {
        val slotMax = slots[slot].first
        val slotActual = this[slot]
        return if (!slotActual.isEmpty) min(slotMax, slotActual.maxStackSize)
        else slotMax
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = true

    override fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
        this.forEachIndexed { index, itemStack -> tag.put(index.toString(), itemStack.serializeNBT()) }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        nbt.allKeys.forEach { this[it.toInt()] = ItemStack.of(nbt.getCompound(it)) }
    }
}