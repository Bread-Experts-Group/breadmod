package breadmod.client.screen.tool_gun.creator

import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.ModToolGunModeProvider.Companion.SCREEN_CONTROL
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.IHoldScreen
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.render.rgMinecraft
import net.minecraft.client.KeyMapping
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
    override val keyCheck: KeyMapping = rgMinecraft.options.keyMappings.first { it.name == SCREEN_CONTROL.nameKey }

    override fun init() {
        super.init()
        rgMinecraft.gameRenderer.loadEffect(modLocation("shaders/post/gui_blur.json"))
    }

    /**
     * Event to run when this screen is closed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun onClose() {
        super.onClose()
        rgMinecraft.gameRenderer.togglePostEffect()
    }
}