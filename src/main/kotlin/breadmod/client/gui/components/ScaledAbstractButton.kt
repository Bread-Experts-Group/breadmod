package breadmod.client.gui.components

import breadmod.util.render.minecraft
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import java.awt.Color

/** A scalable button widget.
 * ### Note: text currently does not support scrolling */
abstract class ScaledAbstractButton(
    private val pX: Int,
    private val pY: Int,
    private val pWidth: Int,
    private val pHeight: Int,
    private val pScale: Double,
    private val pMessage: Component
) : AbstractButton(pX, pY, pWidth, pHeight, pMessage) {
    // todo look into converting clicked, isMouseOver, and renderWidget into using doubles for their positioning instead of using the superclass positioning
    // todo look into implementing OnPress from Button.java (might not be possible)

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val poseStack = pGuiGraphics.pose()
        pGuiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        poseStack.pushPose()
        poseStack.translate(pX.toDouble(), pY.toDouble(), 0.0)
        poseStack.scaleFlat(pScale.toFloat())
        pGuiGraphics.blitNineSliced(WIDGETS_LOCATION, 0, 0, pWidth, pHeight,
            20, 4, 200, 20, 0, textureY
        )
//        renderString(pGuiGraphics, font, fgColor or (Mth.ceil(this.alpha * 255.0f) shl 24))
        // todo re-implementation of scrolling string that supports scaling.
//        AbstractWidget.renderScrollingString(pGuiGraphics, font, pMessage, 0, 0, pWidth, pHeight, Color.WHITE.rgb)
        pGuiGraphics.drawString(minecraft.font, pMessage, 2, 1, Color.WHITE.rgb, true)
        poseStack.popPose()
    }

    override fun renderScrollingString(pGuiGraphics: GuiGraphics, pFont: Font, pWidth: Int, pColor: Int) {
        super.renderScrollingString(pGuiGraphics, pFont, pWidth, pColor)
    }

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