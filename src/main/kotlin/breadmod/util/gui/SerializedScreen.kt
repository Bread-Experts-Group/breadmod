package breadmod.util.gui

import breadmod.ModMain.LOGGER
import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.client.gui.screens.Screen
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
    /**
     * The player's inventory.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected val pInventory: Inventory,
    pTitle: Component,
    protected val rootWidget: ContainerWidget
) : Screen(pTitle), MenuAccess<T> {
    /**
     * Adds the primary [ContainerWidget] to the screen for rendering.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun init() {
        addRenderableWidget(rootWidget)
    }

    /**
     * Whether to pause local game logic when this screen is displayed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun isPauseScreen(): Boolean = false

    /**
     * Returns the provided [pMenu].
     * @return The menu this [SerializedScreen] was created with.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun getMenu(): T = pMenu

    /**
     * Pushes mouse inputs to the provided [rootWidget].
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @return Whether the mouse click was handled by the [rootWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        LOGGER.info("Mouse clicked at $pMouseX, $pMouseY with button $pButton")
        LOGGER.info("Root widget at ${rootWidget.x}, ${rootWidget.y} with size ${rootWidget.width}, ${rootWidget.height}")

        if (
            pMouseX >= rootWidget.x && pMouseY >= rootWidget.y &&
            pMouseX <= (rootWidget.width + rootWidget.x) && pMouseY <= (rootWidget.height + rootWidget.y)
        ) return rootWidget.mouseClicked(pMouseX, pMouseY, pButton)
        return false
    }
}