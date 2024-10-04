package bread.mod.breadmod.client.gui

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.ModMainCommon.modTranslatable
import bread.mod.breadmod.item.toolGun.ToolGunItem
import bread.mod.breadmod.item.toolGun.ToolGunItem.Companion.TOOL_GUN_DEF
import bread.mod.breadmod.util.render.drawScaledText
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import java.awt.Color

class ToolGunOverlay : LayeredDraw.Layer {
    private val overlayTexture = modLocation("textures", "gui", "hud", "tool_gun_overlay.png")
    private val textColor = Color.WHITE.rgb

    override fun render(
        guiGraphics: GuiGraphics,
        deltaTracker: DeltaTracker
    ) {
        val poseStack = guiGraphics.pose()
        val screenWidth = rgMinecraft.window.screenWidth
        val screenHeight = rgMinecraft.window.screenHeight
        val x = screenWidth - (screenWidth - 3)
        val y = screenHeight - (screenHeight - 3)
        val player = rgMinecraft.player ?: return
        val handStack = player.getItemInHand(InteractionHand.MAIN_HAND) ?: return
        val item = handStack.item

        if (!rgMinecraft.options.hideGui && item is ToolGunItem) {
            RenderSystem.enableBlend()
            renderBackground(guiGraphics, poseStack, x, y)

//            val ensured = item.ensureCurrentMode(handStack)
            renderMode(
                /*ensured.getString(MODE_NAMESPACE_TAG)*/ "breadmod", /*item.getCurrentMode(handStack)*/
                guiGraphics, poseStack, x, y
            )

            RenderSystem.disableBlend()
        }
    }

    private fun renderBackground(guiGraphics: GuiGraphics, poseStack: PoseStack, x: Int, y: Int) {
        poseStack.pushPose()
        guiGraphics.blit(overlayTexture, x, y, 0, 0, 166, 41)
        poseStack.popPose()
    }

    private fun renderMode(
        namespace: String,
//        mode: ModToolGunModeDataLoader.ToolgunMode?,
        guiGraphics: GuiGraphics,
        poseStack: PoseStack,
        pX: Int,
        pY: Int
    ) {
        poseStack.pushPose()
        // Icon renders
        guiGraphics.blit(overlayTexture, pX + 1, pY + 33, 0, 41, 8, 8)

        // start rendering key (with the key letter on them) and mouse icons, fix positioning on description and controls, set up 9 sliced key texture for wider keys

        // Action source
        drawScaledText(
            Component.literal(namespace).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC),
            poseStack, guiGraphics, pX + 2, pY + 2, textColor, 0.8f, true
        )

        // Action Name
        drawScaledText(
            (/*mode?.displayName?.copy() ?:*/ Component.literal("???")).withStyle(ChatFormatting.BOLD),
            poseStack, guiGraphics, pX - 1, pY + 4, textColor, 2.5f, false
        )
        // Mode Tooltip
        drawScaledText(
            (/*mode?.tooltip?.copy() ?:*/ modTranslatable(TOOL_GUN_DEF, "broken_tooltip")),
            poseStack,
            guiGraphics,
            pX + 13,
            pY + 43,
            textColor,
            0.4f,
            true
        )
        // KeyBinds
//        mode?.keyBinds?.forEachIndexed { index, control ->
//            val moved = ((index + 1) * 12) + 2
//            drawScaledText(
//                toolGunBindList[control]!!.translatedKeyMessage.copy()
//                    .withStyle { it.withColor(ChatFormatting.GOLD).withItalic(true) }
//                    .append(control.toolGunComponent.copy().withStyle(ChatFormatting.WHITE)),
//                poseStack, guiGraphics,
//                pX + 10, pY + 43 + moved, textColor, 1f, true
//            )
//            toolGunBindList[control]?.key = InputConstants.getKey("key.mouse.right")
//            when (toolGunBindList[control]?.key) {
//                getInput("key.mouse.right") -> {
//                    poseStack.pushPose()
//                    poseStack.scaleFlat(0.68f)
//                    guiGraphics.blit(overlayTexture, pX, pY + 69 + moved, 240, 63, 16, 16)
//                    poseStack.popPose()
//                }
//
//                getInput("key.mouse.middle") -> {
//                    poseStack.pushPose()
//                    poseStack.scaleFlat(0.68f)
//                    guiGraphics.blit(overlayTexture, pX, pY + 73 + moved, 240, 47, 16, 16)
//                    poseStack.popPose()
//                }
//            }
//        }
        poseStack.popPose()
    }
}