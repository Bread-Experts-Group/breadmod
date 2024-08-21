package breadmod.client.gui.components

import breadmod.util.render.minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import java.awt.Color

class TestButton(
    pX: Int,
    pY: Int,
    pWidth: Int,
    pHeight: Int
) : CustomAbstractWidget(pX, pY, pWidth, pHeight, "button", Component.empty()) {
    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        pGuiGraphics.fill(RenderType.gui(), x, y, x + width, y + height, Color.GRAY.rgb)
        pGuiGraphics.drawString(minecraft.font, "test", x, y, Color.WHITE.rgb)
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
        println("old: $x, $y")
        println("move")
        move(50, 50)
        println("new: $x, $y")
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput)
    }
}