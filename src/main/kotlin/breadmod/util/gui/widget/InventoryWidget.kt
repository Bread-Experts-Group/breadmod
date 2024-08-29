package breadmod.util.gui.widget

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

/**
 * A widget that renders a given [Inventory].
 * @param pX The X position this widget will render at.
 * @param pY The Y position this widget will render at.
 * @param pInventory The [Inventory] to render.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class InventoryWidget(
    pX: Int, pY: Int,
    pInventory: Inventory,
    pContainerMenu: AbstractContainerMenu
) : ItemContainerWidget(pX, pY, 160, 74, pInventory, pContainerMenu) {
    private fun addSlotWidget(c: Int, r: Int, yOffset: Int = 0) {
        val linear = (r * 9) + c
        SlotWidget(c * 18, (r * 18) + yOffset, this, linear)
    }

    init {
        for (rI in 3 downTo 1) repeat(9) { cI -> addSlotWidget(cI, rI, -18) }
        repeat(9) { cI -> addSlotWidget(cI, 0, 58) }
    }
}