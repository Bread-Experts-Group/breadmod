package breadmod.client.gui.components

import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import java.awt.Color

class DraggableWidget(
    private val pX: Int,
    private val pY: Int,
    private val pWidth: Int,
    private val pHeight: Int,
    private val pScale: Double,
    private val pMessage: Component
) : AbstractWidget(pX, pY, pWidth, pHeight, pMessage) {
    private var dragX: Double = pX.toDouble()
    private var dragY: Double = pY.toDouble()
    private val font: Font = Minecraft.getInstance().font

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val poseStack = pGuiGraphics.pose()
        pGuiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        poseStack.pushPose()
        poseStack.translate(dragX, dragY, 0.0)
        poseStack.scaleFlat(pScale.toFloat())
        pGuiGraphics.fill(RenderType.gui(), 0, 0, width, height, Color(26, 26, 26).rgb)
        pGuiGraphics.fill(RenderType.gui(), 1, 1, width - 1, height - 1, Color(51, 51, 51).rgb)
        pGuiGraphics.drawString(font, pMessage, 2, 1, Color.WHITE.rgb, true)
        poseStack.popPose()
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if(visible) {
            isHovered = pMouseX >= dragX && pMouseY >= dragY && pMouseX < dragX + (width * pScale) && pMouseY < dragY + (height * pScale)
            renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            updateTooltip()
        }
    }

    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        return if(visible && isFocused) {
//            println("hello :D")
            dragX += pDragX
            dragY += pDragY
            true
        } else false
    }

    override fun onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double) {
        println("mouse dragging: X:$pMouseX, y:$pMouseY")
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
//        dragX = 300.0
//        dragY = 200.0
    }

    // the hitbox for the widget
    override fun clicked(pMouseX: Double, pMouseY: Double): Boolean =
        active && visible && pMouseX >= dragX && pMouseY >= dragY &&
                pMouseX < dragX + (width * pScale) && pMouseY < dragY + (height * pScale)

    // controls the "hover" graphic when mouse is over widget
    override fun isMouseOver(pMouseX: Double, pMouseY: Double): Boolean =
        active && visible && pMouseX >= dragX && pMouseY >= dragY &&
                pMouseX < dragX + (width * pScale) && pMouseY < dragY + (height * pScale)

    override fun isFocused(): Boolean = isHovered
}