package breadmod.util.gui.widget

import breadmod.util.render.rgMinecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import java.awt.Color

/**
 * A widget that displays text.
 * @param pX The X position of the widget.
 * @param pY The Y position of the widget.
 * @param pWidth The width of the widget.
 * @param pHeight The height of the widget.
 * @param pMessage The component to display as text.
 * @param pMessageColor The color of the text.
 * @param pBackgroundColor The color of the background.
 * @param pCentering The centering method to use when rendering the text.
 * @param pWrapping The wrapping method to use when rendering the text.
 * @param pNoScissor If true, the text will be rendered without scissoring,
 * allowing it to be rendered outside the widget's bounds.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class TextWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,

    pMessage: Component,
    private val pMessageColor: Color = Color.WHITE,
    private val pBackgroundColor: Color? = null,

    private val pCentering: CenteringType = CenteringType.CENTER_LINE,
    private val pWrapping: WrappingType = WrappingType.SCROLL_HORIZONTAL,
    /**
     * If true, the text will be rendered without scissoring, allowing it to be rendered outside the widget's bounds.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    private val pNoScissor: Boolean = false
) : AbstractWidget(pX, pY, pWidth, pHeight, pMessage) {
    /**
     * Centering method used when rendering text.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    enum class CenteringType {
        /**
         * Centers the text on the horizontal axis.
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        CENTER_LINE,

        /**
         * Centers the text on the horizontal and vertical axis within the widget's bounds.
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        CENTER_RECT,

        /**
         * No centering is applied to the text. It is rendered from top-left corner of the widget.
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        NONE
    }

    /**
     * Wrapping method used when rendering text.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    enum class WrappingType {
        /**
         * Scrolls the text from side to side to reveal the full message, like a button widget.
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        SCROLL_HORIZONTAL,

        /**
         * Wraps the text to the next line if it exceeds the widget's width, clipping the text if it exceeds the height.
         * @see pNoScissor
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        WRAP_VERTICAL,

        /**
         * No wrapping is applied to the text, and will be clipped if it exceeds the widget's width or height.
         * @see pNoScissor
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        NONE
    }

    private val getCenterForFont = (height / 2) - (rgMinecraft.font.lineHeight / 2)

    /**
     * Renders this [TextWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if (!pNoScissor) pGuiGraphics.enableScissor(x, y, x + width, y + height)
        if (pBackgroundColor != null && pBackgroundColor.alpha > 0)
            pGuiGraphics.fill(0, 0, width, height, pBackgroundColor.rgb)

        if (pMessageColor.alpha > 0) {
            val tooBig = rgMinecraft.font.width(message) > width
            when {
                tooBig && pWrapping == WrappingType.SCROLL_HORIZONTAL -> renderScrollingString(
                    pGuiGraphics, rgMinecraft.font, message,
                    0, 0, width, if (pCentering == CenteringType.CENTER_LINE) rgMinecraft.font.lineHeight else height,
                    pMessageColor.rgb
                )

                tooBig && pWrapping == WrappingType.WRAP_VERTICAL ->
                    pGuiGraphics.drawWordWrap(rgMinecraft.font, message, 0, 0, width, pMessageColor.rgb)

                pCentering != CenteringType.NONE -> {
                    val position = Vector2i(width / 2, 0)
                    if (pCentering == CenteringType.CENTER_RECT) position.add(0, getCenterForFont)

                    pGuiGraphics.drawCenteredString(rgMinecraft.font, message, position.x, position.y, pMessageColor.rgb)
                }

                else -> pGuiGraphics.drawString(rgMinecraft.font, message, 0, 0, pMessageColor.rgb)
            }
        }

        if (!pNoScissor) pGuiGraphics.disableScissor()
    }

    /**
     * Updates the narration output of this [TextWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, message)
    }
}