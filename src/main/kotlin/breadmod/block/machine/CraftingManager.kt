package breadmod.block.machine

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.capability.IndexableItemHandler
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack

class CraftingManager<T: AbstractMachineBlockEntity<*>>(
    private val itemHandler: IndexableItemHandler,
    private val viewSlots: List<Int>,
    private val width: Int,
    private val height: Int,
    val parent: T
): CraftingContainer {
    init {
        require(viewSlots.size <= itemHandler.size) { "View slots size must not exceed item handler size" }
    }

    override fun clearContent() { viewSlots.forEach { itemHandler[it].count = 0 } }
    override fun getContainerSize(): Int = viewSlots.size
    override fun isEmpty(): Boolean = viewSlots.sumOf { itemHandler[it].count } == 0

    private fun contains(pSlot: Int) = viewSlots.contains(pSlot)

    override fun getItem(pSlot: Int): ItemStack =
        if(contains(pSlot)) itemHandler[pSlot] else ItemStack.EMPTY
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack =
        if(contains(pSlot)) itemHandler[pSlot].split(pAmount) ?: ItemStack.EMPTY else ItemStack.EMPTY
    override fun removeItemNoUpdate(pSlot: Int): ItemStack =
        if(contains(pSlot)) itemHandler[pSlot].copyAndClear() ?: ItemStack.EMPTY else ItemStack.EMPTY
    override fun setItem(pSlot: Int, pStack: ItemStack) {
        if(contains(pSlot)) itemHandler[pSlot] = pStack }
    override fun stillValid(pPlayer: Player): Boolean = true
    override fun fillStackedContents(pContents: StackedContents) {
        itemHandler[0].let { pContents.accountStack(it) } }

    override fun getWidth(): Int = width
    override fun getHeight(): Int = height
    override fun getItems(): MutableList<ItemStack> = itemHandler.slice(viewSlots).toMutableList()
    override fun setChanged() { itemHandler.changed?.invoke() }
}