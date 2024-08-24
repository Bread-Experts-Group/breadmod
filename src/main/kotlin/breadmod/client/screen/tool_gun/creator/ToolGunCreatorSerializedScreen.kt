package breadmod.client.screen.tool_gun.creator

import breadmod.datagen.tool_gun.ModToolGunModeProvider.Companion.SCREEN_CONTROL
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.IHoldScreen
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
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
    override val keyCheck: KeyMapping =
        breadmod.util.render.minecraft.options.keyMappings.first { it.name == SCREEN_CONTROL.nameKey }

    override fun init() {
        super.init()
        println("!")
        try {
            println("G")
            breadmod.util.render.minecraft.gameRenderer.loadEffect(ResourceLocation("shaders/post/antialias.json"))
            println("B")
        } catch (e: Throwable) {
            println(e.stackTraceToString())
        }
    }

    override fun onClose() {
        super.onClose()
        breadmod.util.render.minecraft.gameRenderer.togglePostEffect()
    }
}