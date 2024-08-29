package breadmod.util.gui

import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.render.mouseGuiX
import breadmod.util.render.mouseGuiY
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
     * Pushes mouse click inputs to the provided [rootWidget].
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @return Whether the mouse click was handled by the [rootWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        if (!rootWidget.isMouseOver(pMouseX, pMouseY) || !rootWidget.mouseClicked(pMouseX, pMouseY, pButton))
            super.mouseClicked(pMouseX, pMouseY, pButton)
        else false

    /**
     * Pushes mouse movement inputs to the provided [rootWidget].
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @return Whether the mouse click was handled by the [rootWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseMoved(pMouseX: Double, pMouseY: Double) {
        if (rootWidget.isMouseOver(pMouseX, pMouseY)) rootWidget.mouseMoved(pMouseX, pMouseY)
        else super.mouseMoved(pMouseX, pMouseY)
    }

    /**
     * Pushes mouse dragging inputs to the provided [rootWidget].
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @param pDragX The X distance of the mouse drag.
     * @param pDragY The Y distance of the mouse drag.
     * @return Whether the mouse click was handled by the [rootWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean =
        if (
            !rootWidget.isMouseOver(mouseGuiX, mouseGuiY) ||
            !rootWidget.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        ) super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        else false

    /**
     * Pushes key presses to the provided [rootWidget].
     * @param pKeyCode The key code of the key pressed.
     * @param pScanCode The scan code of the key pressed.
     * @param pModifiers The bit-mapped modifiers of the key pressed.
     * @return Whether the key press was handled by the [rootWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean =
        if (
            !rootWidget.isMouseOver(mouseGuiX, mouseGuiY) ||
            !rootWidget.keyPressed(pKeyCode, pScanCode, pModifiers)
        ) super.keyPressed(pKeyCode, pScanCode, pModifiers)
        else false

    /**
     * Pushes key releases to the provided [rootWidget].
     * @param pKeyCode The key code of the key pressed.
     * @param pScanCode The scan code of the key pressed.
     * @param pModifiers The bit-mapped modifiers of the key pressed.
     * @return Whether the key press was handled by the [rootWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean =
        if (
            !rootWidget.isMouseOver(mouseGuiX, mouseGuiY) ||
            !rootWidget.keyReleased(pKeyCode, pScanCode, pModifiers)
        ) super.keyReleased(pKeyCode, pScanCode, pModifiers)
        else false
}