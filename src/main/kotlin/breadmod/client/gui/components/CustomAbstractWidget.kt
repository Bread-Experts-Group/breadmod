package breadmod.client.gui.components

import breadmod.ModMain.LOGGER
import breadmod.util.render.minecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

abstract class CustomAbstractWidget(
    pX: Int,
    pY: Int,
    pWidth: Int,
    pHeight: Int,
    /** A unique widget identifier, used in modifying certain elements such as position. */
    val pId: String,
    pMessage: Component
) : AbstractWidget(pX, pY, pWidth, pHeight, pMessage) {
    protected val children: MutableMap<String, CustomAbstractWidget> = mutableMapOf()
    protected var relativeX: Int = pX
    protected var relativeY: Int = pY

    fun move(pChangeX: Int, pChangeY: Int) {
        x += pChangeX; y += pChangeY
        relativeX += pChangeX
        relativeY += pChangeY
        children.forEach { child -> child.value.move(pChangeX, pChangeY) }
    }

    fun addChild(element: CustomAbstractWidget, id: String) {
        if (!checkWidgetIdExists(id)) {
            children["${pId}_$id"] = element
        } else LOGGER.error("Child widget with id: $id already exists!")
    }

    fun removeChild(id: String) {
        if (checkWidgetIdExists(id)) {
            children.remove("${pId}_$id")
        } else LOGGER.error("Child widget with id: $id does not exist!")
    }

    /**
     * Clears the [children] map of all child widgets
     */
    fun removeAllChildren() = children.clear()

    private fun checkWidgetIdExists(id: String): Boolean = children[id] != null

    open fun nestedMouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): CustomAbstractWidget? {
        for (i in 0..<children.size) {
            val list = children.entries.elementAt(i).value
            val result = list.nestedMouseClicked(pMouseX, pMouseY, pButton)
            if (result != null) return result
        }
        if (active && visible && isValidClickButton(pButton) && clicked(pMouseX, pMouseY)) {
            playDownSound(minecraft.soundManager)
            onClick(pMouseX, pMouseY)
            return this
        }
        return null
    }

    override fun isValidClickButton(pButton: Int): Boolean =
        pButton == InputConstants.MOUSE_BUTTON_LEFT || pButton == InputConstants.MOUSE_BUTTON_RIGHT ||
                pButton == InputConstants.MOUSE_BUTTON_MIDDLE

    final override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        nestedMouseClicked(pMouseX, pMouseY, pButton) != null

    override fun isMouseOver(pMouseX: Double, pMouseY: Double): Boolean {
        children.forEach { child -> child.value.isMouseOver(pMouseX, pMouseY) }
//        for (i in 0..<children.size) {
//            val list = children.entries.elementAt(i).value
//            return list.isMouseOver(pMouseX, pMouseY)
//        }
        return super.isMouseOver(pMouseX, pMouseY)
    }

//    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
//        for (i in 0..<children.size) {
//            val list = children.entries.elementAt(i)
//            return if (list.value.isValidClickButton(pButton)) {
//                list.value.mouseClicked(pMouseX, pMouseY, pButton)
//            } else false
//        }
//        return super.mouseClicked(pMouseX, pMouseY, pButton)
//    }

    override fun onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double) {
        children.forEach { child -> child.value.onDrag(pMouseX, pMouseY, pDragX, pDragY) }
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY)
    }

    override fun onRelease(pMouseX: Double, pMouseY: Double) {
        children.forEach { child -> child.value.onRelease(pMouseX, pMouseY) }
        super.onRelease(pMouseX, pMouseY)
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        children.forEach { child -> child.value.keyPressed(pKeyCode, pScanCode, pModifiers) }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun charTyped(pCodePoint: Char, pModifiers: Int): Boolean {
        children.forEach { child -> child.value.charTyped(pCodePoint, pModifiers) }
        return super.charTyped(pCodePoint, pModifiers)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if (this.visible) {
            this.isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
            this.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            children.forEach{ child -> child.value.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)}
            this.updateTooltip()
        }
    }
}