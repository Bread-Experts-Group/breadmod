package breadmod.menu

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot

abstract class AbstractModContainerMenu(
    pMenuType: MenuType<*>,
    pContainerId: Int
) : AbstractContainerMenu(pMenuType, pContainerId) {
    fun addInventorySlots(pInventory: Inventory, pX: Int, pHotBarY: Int, pInventoryY: Int) {
        repeat(9) { addSlot(Slot(pInventory, it, pX + it * 18, pHotBarY)) }
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
}