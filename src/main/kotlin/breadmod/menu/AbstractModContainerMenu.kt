package breadmod.menu

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class AbstractModContainerMenu(
    pMenuType: MenuType<*>,
    pContainerId: Int
) : AbstractContainerMenu(pMenuType, pContainerId) {
    fun addInventorySlots(pInventory: Inventory, pX: Int, pHotbarY: Int, pInventoryY: Int) {
        repeat(9) { addSlot(Slot(pInventory, it, pX + it * 18, pHotbarY)) }
        repeat(3) { y ->
            repeat(9) { x ->
                addSlot(
                    Slot(
                        pInventory,
                        x + y * 9 + 9,
                        pX + x * 18,
                        pInventoryY + y * 18
                    )
                )
            }
        }
    }

    override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack = moveStackFunction(pPlayer, pIndex)

    /**
     * ### Used in [quickMoveStack] to enable shift clicking items into the target inventory
     * value must match the number of slots your block entity has
     */
    open val containerSlotCount: Int = 1

    private fun moveStackFunction(playerIn: Player, pIndex: Int):ItemStack {
        val sourceSlot = slots[pIndex]
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM

        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(
                    sourceStack,
                    TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + containerSlotCount,
                    false
                )
            ) return ItemStack.EMPTY // EMPTY_ITEM
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + containerSlotCount) {
            // This is a BE slot so merge the stack into the player's inventory
            if (!moveItemStackTo(
                    sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false
                )
            ) return ItemStack.EMPTY
        } else return ItemStack.EMPTY
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else sourceSlot.setChanged()
        sourceSlot.onTake(playerIn, sourceStack)
        return copyOfSourceStack
    }

    private companion object {
        // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
        // must assign a slot number to each of the slots used by the GUI.
        // For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
        // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
        //  0 – 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 – 8)
        //  9 – 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 – 35)
        //  36 – 44 = TileInventory slots, which map to our BlockEntity slot numbers 0 – 8)
        const val HOTBAR_SLOT_COUNT = 9
        const val PLAYER_INVENTORY_ROW_COUNT = 3
        const val PLAYER_INVENTORY_COLUMN_COUNT = 9
        const val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        const val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        const val VANILLA_FIRST_SLOT_INDEX = 0
        const val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT
    }
}