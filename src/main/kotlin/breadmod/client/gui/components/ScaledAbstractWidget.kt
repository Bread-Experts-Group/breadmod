package breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

abstract class ScaledAbstractWidget(
    pX: Int,
    pY: Int,
    pWidth: Int,
    pHeight: Int,
    private val pScale: Double,
    pMessage: Component
) : AbstractWidget(pX, pY, pWidth, pHeight, pMessage) {
    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput)
    }

    // todo look into converting clicked, isMouseOver, and renderWidget into using doubles for their positioning instead of using the superclass positioning

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if(visible) {
            isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + (width * pScale) && pMouseY < y + (height * pScale)
            renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            updateTooltip()
        }
    }

    // the hitbox for the widget
    override fun clicked(pMouseX: Double, pMouseY: Double): Boolean =
        active && visible && pMouseX >= x && pMouseY >= y &&
                pMouseX < x + (width * pScale) && pMouseY < y + (height * pScale)

    // controls the "hover" graphic when mouse is over widget
    override fun isMouseOver(pMouseX: Double, pMouseY: Double): Boolean =
        active && visible && pMouseX >= x && pMouseY >= y &&
                pMouseX < x + (width * pScale) && pMouseY < y + (height * pScale)

    override fun isFocused(): Boolean = isHovered
}