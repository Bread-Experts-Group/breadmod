package org.bread_experts_group.breadmod.experimental.tool_gun_mode

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.util.render.rgMinecraft
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

// todo proof of concept
//  needs proper gui centering, and actual logic for putting together selectable modes and previews
class TestScreen(title: Component) : Screen(title) {
    var leftPos = (width - 280) / 2
    var topPos = (height - 210) / 2

    val modes: List<ModeWidget> = listOf(
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget(),
        ExplodeWidget()
    )

    var gridList: List<Pair<Int, Int>> = listOf()

    override fun isPauseScreen(): Boolean = false

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
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
        if (focused is ModeWidget) {
            val widget = focused as ModeWidget
            guiGraphics.fill(RenderType.gui(), leftPos + 179, topPos + 3, leftPos + 296, topPos + 70, Color.BLACK.rgb)
            guiGraphics.drawString(
                rgMinecraft.font,
                widget.modeName,
                leftPos + 179,
                topPos + 73,
                Color.BLACK.rgb,
                false
            )
            guiGraphics.fill(RenderType.gui(), leftPos + 176, topPos + 83, leftPos + 299, topPos + 84, Color.RED.rgb)
            guiGraphics.drawWordWrap(
                rgMinecraft.font,
                widget.modeDescription,
                leftPos + 179,
                topPos + 86,
                120,
                Color.BLACK.rgb
            )
            poseStack.translate(leftPos + 179.8f, topPos + 4f, 0f)
            poseStack.scaleFlat(0.135f)
            guiGraphics.blit(
                widget.previewImage,
                0,
                0,
                0f,
                0f,
                854,
                480,
                854,
                480
            )
        } else {
            guiGraphics.drawWordWrap(
                rgMinecraft.font,
                Component.literal("Click on any of the modes to display their preview."),
                leftPos + 179,
                topPos + 3,
                125,
                Color.WHITE.rgb
            )
        }
        poseStack.popPose()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
        if (keyCode == InputConstants.KEY_E) {
            onClose()
            true
        } else super.keyPressed(keyCode, scanCode, modifiers)

    override fun init() {
        leftPos = (width - 280) / 2
        topPos = (height - 210) / 2
        gridList = buildList {
            repeat(5) { y ->
                repeat(4) { x ->
                    add(leftPos + 10 + x * 40 to topPos + 15 + y * 45)
                }
            }
        }

        repeat(modes.size) { index ->
            modes[index].x = gridList[index].first
            modes[index].y = gridList[index].second
        }
        modes.forEach {
            addRenderableWidget(it)
        }
    }
}