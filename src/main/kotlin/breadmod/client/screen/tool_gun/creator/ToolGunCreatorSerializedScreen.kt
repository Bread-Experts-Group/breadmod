package breadmod.client.screen.tool_gun.creator

import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.ModToolGunModeProvider.Companion.SCREEN_CONTROL
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.IHoldScreen
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.render.PostProcessingRegistry
import breadmod.util.render.rgMinecraft
import breadmod.util.render.shaderPreCompilation
import net.minecraft.client.KeyMapping
import net.minecraft.client.renderer.PostChain
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

/**
 * Creator mode's variation of the [SerializedScreen] class, which closes if the player doesn't hold down the
 * screen opening key. (Overridable)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
internal class ToolGunCreatorSerializedScreen(
    pMenu: ToolGunCreatorMenu,
    pInventory: Inventory,
    pTitle: Component,
    rootWidget: ContainerWidget
) : SerializedScreen<ToolGunCreatorMenu>(pMenu, pInventory, pTitle, rootWidget), IHoldScreen {
    /**
     * Prevents this screen from closing if an edit box is currently focused.
     */
    var hasActiveEditBox: Boolean = false
    override var shouldClose: Boolean = !hasActiveEditBox
    override val keyCheck: KeyMapping = rgMinecraft.options.keyMappings.first { it.name == SCREEN_CONTROL.nameKey }

    private companion object {
        val blurChainLoc = modLocation("shaders/post/gui_blur.json")
        var blurChain: PostChain? = null
        const val POST_PROCESSING_ENTRY_BLUR_NAME = "ToolGun Creator Mode GUI Blur"
    }

    override fun init() {
        super.init()
        if (blurChain == null) {
            shaderPreCompilation[blurChainLoc.toString()] = { _, _, _, _, _ ->
                TODO("Figure it out. https://ktstephano.github.io/rendering/opengl/ssbos")
            }

            blurChain = PostChain(
                rgMinecraft.textureManager, rgMinecraft.resourceManager,
                rgMinecraft.mainRenderTarget, blurChainLoc
            )
        }
        PostProcessingRegistry.addProcessor(POST_PROCESSING_ENTRY_BLUR_NAME, blurChain!!)
    }

    /**
     * Event to run when this screen is closed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun onClose() {
        super.onClose()
        PostProcessingRegistry.removeProcessor(POST_PROCESSING_ENTRY_BLUR_NAME)
    }
}