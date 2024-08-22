package breadmod.client.screen.tool_gun.creator

import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.IHoldScreen
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

/**
 * Creator mode's variation of the [SerializedScreen] class, which closes if the player doesn't hold down the
 * screen opening key. (Overridable)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ToolGunCreatorSerializedScreen(
    pMenu: ToolGunCreatorMenu,
    pInventory: Inventory,
    pTitle: Component,
    rootWidget: ContainerWidget
) : SerializedScreen<ToolGunCreatorMenu>(pMenu, pInventory, pTitle, rootWidget), IHoldScreen {
    override var shouldClose: Boolean = true
}