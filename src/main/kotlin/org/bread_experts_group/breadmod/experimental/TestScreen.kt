package org.bread_experts_group.breadmod.experimental

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.util.render.rgMinecraft
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

// todo proof of concept
//  needs proper gui centering, and actual logic for putting together selectable modes and previews
class TestScreen(title: Component) : Screen(title) {
    var leftPos = width / 2
    var topPos = height / 2

    override fun isPauseScreen(): Boolean = false

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(leftPos + 85f, topPos + 10f, 0f)
        guiGraphics.fill(RenderType.gui(), leftPos, topPos, leftPos + 173, topPos + 200, Color.RED.rgb)
        guiGraphics.fill(
            RenderType.gui(),
            leftPos + 1,
            topPos + 1,
            leftPos + 172,
            topPos + 199,
            Color(150, 150, 150).rgb
        )


        guiGraphics.fill(RenderType.gui(), leftPos + 175, topPos, leftPos + 300, topPos + 200, Color.RED.rgb)
        guiGraphics.fill(
            RenderType.gui(),
            leftPos + 176,
            topPos + 1,
            leftPos + 299,
            topPos + 199,
            Color(150, 150, 150).rgb
        )

        guiGraphics.drawString(rgMinecraft.font, title, leftPos + 2, topPos + 2, Color.BLACK.rgb, false)
        guiGraphics.fill(RenderType.gui(), leftPos + 179, topPos + 3, leftPos + 296, topPos + 70, Color.BLACK.rgb)
        guiGraphics.drawString(
            rgMinecraft.font,
            Component.literal("example text"),
            leftPos + 179,
            topPos + 75,
            Color.BLACK.rgb,
            false
        )
        poseStack.translate(leftPos + 179.8f, topPos + 4f, 0f)
        poseStack.scaleFlat(0.06f)
        guiGraphics.blit(
            modLocation("textures", "gui", "tool_gun", "exploder.png"),
            0,
            0,
            0f,
            0f,
            1920,
            1080,
            1920,
            1080
        )
        poseStack.popPose()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
        if (keyCode == InputConstants.KEY_E) {
            onClose()
            true
        } else super.keyPressed(keyCode, scanCode, modifiers)

    override fun init() {
        addRenderableWidget(
            ModeWidget(
                leftPos + 90,
                topPos + 25,
                Component.literal("Exploder"),
                Blocks.TNT.asItem().defaultInstance
            )
        )
    }

    class ModeWidget(
        val startX: Int,
        val startY: Int,
        message: Component,
        val icon: ItemStack
    ) : AbstractWidget(startX, startY, 35, 40, message) {

        override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
            guiGraphics.fill(
                RenderType.gui(),
                startX,
                startY,
                startX + 35,
                startY + 40,
                if (isHovered) Color(16755200).rgb else Color.GRAY.rgb
            )
            guiGraphics.fill(RenderType.gui(), startX + 1, startY + 1, startX + 34, startY + 39, Color.DARK_GRAY.rgb)
            guiGraphics.drawScrollingString(
                rgMinecraft.font,
                message,
                startX + 2,
                startX + 33,
                startY + 29,
                Color.WHITE.rgb
            )
            guiGraphics.pose().translate(startX.toFloat() + 5.5f, startY.toFloat() + 2, 0f)
            guiGraphics.pose().scaleFlat(1.5f)
            guiGraphics.renderFakeItem(icon, 0, 0)
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        }

    }
}