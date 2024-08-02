package breadmod.item.tool_gun.mode

import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class ToolGunCreatorMenu(
    pContainerId: Int,
    pInventory: Inventory,
): AbstractContainerMenu(
    ModMenuTypes.TOOL_GUN_CREATOR.get(),
    pContainerId
) {
    override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(pPlayer: Player): Boolean = pPlayer.isHolding(ModItems.TOOL_GUN.get())
}