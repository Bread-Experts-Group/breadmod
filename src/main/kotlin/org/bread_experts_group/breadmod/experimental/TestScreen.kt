package org.bread_experts_group.breadmod.experimental

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.util.render.rgMinecraft
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

class TestScreen(title: Component) : Screen(title) {

    override fun isPauseScreen(): Boolean = false

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val poseStack = guiGraphics.pose()
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        val x = width / 2 - 86
        val y = height / 2 - 100

        poseStack.translate(x.toFloat(), y.toFloat(), 0f)
        guiGraphics.fill(RenderType.gui(), 0, 0, 173, 200, Color.RED.rgb)
        guiGraphics.fill(
            RenderType.gui(),
            1,
            1,
            172,
            199,
            Color(150, 150, 150).rgb
        )
        guiGraphics.drawString(rgMinecraft.font, title, 2, 2, Color.BLACK.rgb, false)
        poseStack.scaleFlat(10f)
        poseStack.translate(0.5f, 2f, 0f)
        guiGraphics.renderFakeItem(ModBlocks.BREAD_BLOCK.toStack(), 0, 0)
    }
}