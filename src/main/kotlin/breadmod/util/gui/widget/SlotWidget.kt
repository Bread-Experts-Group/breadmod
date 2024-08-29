package breadmod.util.gui.widget

import breadmod.util.render.rgMinecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import java.awt.Color

/**
 * A widget that renders an item slot.
 * @param pX The X position this slot will render at.
 * @param pY The Y position this slot will render at.
 * @param pContainer The [Container] this slot attaches to.
 * @param slot The slot index in the [pContainer].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SlotWidget(
    pX: Int, pY: Int,
    val pContainer: Container,
    val slot: Int
) : AbstractWidget(pX, pY, 16, 16, Component.empty()), IWidgetMouseClickSensitive {
    private val hoverColor = Color(1f, 1f, 1f, 0.5f).rgb

    /**
     * Renders this [SlotWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val stack = pContainer.getItem(slot)
        val notEmpty = !stack.isEmpty
        val isHoveredLocal = isMouseOver(pMouseX.toDouble(), pMouseY.toDouble())
        if (notEmpty || isHoveredLocal) {
            if (notEmpty) {
                pGuiGraphics.renderItem(stack, 0, 0)
                pGuiGraphics.renderItemDecorations(rgMinecraft.font, stack, 0, 0)
            }
            if (isHoveredLocal) {
                pGuiGraphics.fill(RenderType.guiOverlay(), 0, 0, width, height, hoverColor)
                if (notEmpty) pGuiGraphics.renderTooltip(rgMinecraft.font, stack, pMouseX - x, pMouseY - y)
            }
        }
    }

    /**
     * Handles mouse clicks on this [SlotWidget].
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @return Whether the click was handled by this widget and should be consumed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        pContainer.getItem(slot).shrink(999)
        return true
    }

    /**
     * Updates the narration output of this [SlotWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        val stack = pContainer.getItem(slot)
        pNarrationElementOutput.add(NarratedElementType.HINT, stack.hoverName)
    }
}