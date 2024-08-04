package breadmod.menu.item

import breadmod.menu.AbstractModContainerMenu
import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class ToolGunCreatorMenu(
    pContainerId: Int,
    val inventory: Inventory,
): AbstractModContainerMenu(
    ModMenuTypes.TOOL_GUN_CREATOR.get(),
    pContainerId
) {
    init {
        addInventorySlots(inventory, 3, 201, 144)
    }

    override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(pPlayer: Player): Boolean = pPlayer.isHolding(ModItems.TOOL_GUN.get())
}