package breadmod.util.gui

import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

/**
 * A basic screen that renders a [ContainerWidget].
 * @param T The type of the [AbstractContainerMenu].
 * @param pTitle The title of the screen.
 * @param rootWidget The widget to render.
 * @param pMenu The menu to access.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
open class SerializedScreen<T : AbstractContainerMenu>(
    protected val pMenu: T,
    pInventory: Inventory,
    pTitle: Component,
    protected val rootWidget: ContainerWidget
) : AbstractContainerScreen<T>(pMenu, pInventory, pTitle), MenuAccess<T> {
    /**
     * Adds the primary [ContainerWidget] to the screen for rendering.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun init() {
        super.init()
        addRenderableWidget(rootWidget)
    }

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        //
    }

    /**
     * Returns the provided [pMenu].
     * @return The menu this [SerializedScreen] was created with.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun getMenu(): T = pMenu
}