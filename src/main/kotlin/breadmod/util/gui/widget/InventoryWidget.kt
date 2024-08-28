package breadmod.util.gui.widget

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

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
    private val pInventory: Inventory
) : ContainerWidget(
    pX, pY, 90, 45, 0f, Component.empty(), mutableMapOf()
) {
    private fun addSlotWidget(c: Int, r: Int, yOffset: Int = 0) {
        val linear = (r * 9) + c
        this.addWidget(
            SlotWidget(c * 9, (r * 9) + yOffset, pInventory, linear),
            0.0, "slot$linear"
        )
    }

    init {
        for (rI in 3 downTo 1) repeat(9) { cI -> addSlotWidget(cI, rI) }
        repeat(9) { cI -> addSlotWidget(cI, 0, 38) }
    }
}