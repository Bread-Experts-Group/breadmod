package breadmod.util.gui.widget

import breadmod.util.gui.widget.BackgroundWidget.SolidColorBackgroundWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import java.awt.Color

/**
 * A light widget that renders a visible background.
 * @param pX The X position of the widget.
 * @param pY The Y position of the widget.
 * @param pWidth The width of the widget.
 * @param pHeight The height of the widget.
 * @see SolidColorBackgroundWidget
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class BackgroundWidget private constructor(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
) : AbstractWidget(pX, pY, pWidth, pHeight, Component.empty()) {
    /**
     * Blank narration output for this [BackgroundWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    final override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

    /**
     * A light widget that renders a solid color background.
     * @param pX The X position of the widget.
     * @param pY The Y position of the widget.
     * @param pWidth The width of the widget.
     * @param pHeight The height of the widget.
     * @param pColor The color of the background.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    class SolidColorBackgroundWidget(
        pX: Int, pY: Int,
        pWidth: Int, pHeight: Int,
        private val pColor: Color = Color.WHITE,
        private val pRenderType: RenderType = RenderType.gui()
    ) : BackgroundWidget(pX, pY, pWidth, pHeight) {
        /**
         * Renders this [TextWidget].
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
            pGuiGraphics.fill(pRenderType, 0, 0, width, height, pColor.rgb)
        }
    }
}