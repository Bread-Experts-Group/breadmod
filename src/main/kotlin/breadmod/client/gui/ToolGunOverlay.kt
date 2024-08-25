package breadmod.client.gui

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.tool_gun.ToolGunItem
import breadmod.item.tool_gun.ToolGunItem.Companion.MODE_NAMESPACE_TAG
import breadmod.util.render.rgMinecraft
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraftforge.client.gui.overlay.ForgeGui
import java.awt.Color

class ToolGunOverlay : AbstractModGuiOverlay() {
    private val overlayTexture = modLocation("textures", "gui", "hud", "tool_gun_overlay.png")
    private val textColor = Color.WHITE.rgb

    override fun renderOverlay(
        pGui: ForgeGui,
        pGuiGraphics: GuiGraphics,
        pPartialTick: Float,
        pScreenWidth: Int,
        pScreenHeight: Int,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPlayer: LocalPlayer
    ) {
        val x = pScreenWidth - (pScreenWidth - 3)
        val y = pScreenHeight - (pScreenHeight - 3)
        val handStack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND) ?: return
        val item = handStack.item

        if (!rgMinecraft.options.hideGui && item is ToolGunItem) {
            pGui.setupOverlayRenderState(true, false)
            renderBackground(pGuiGraphics, pPoseStack, x, y)

            val ensured = item.ensureCurrentMode(handStack)
            renderMode(
                ensured.getString(MODE_NAMESPACE_TAG), item.getCurrentMode(handStack),
                pGuiGraphics, pPoseStack, pGui, x, y
            )
        }
    }

    private fun renderBackground(pGuiGraphics: GuiGraphics, pPose: PoseStack, x: Int, y: Int) {
        pPose.pushPose()
        // Main Background Texture
        pGuiGraphics.blit(overlayTexture, x, y, 0, 0, 166, 41)
        pPose.popPose()
    }

    private fun renderMode(
        namespace: String,
        pMode: ModToolGunModeDataLoader.ToolgunMode?,
        pGuiGraphics: GuiGraphics,
        pPose: PoseStack,
        pGui: ForgeGui,
        pX: Int,
        pY: Int
    ) {
        pPose.pushPose()
        // Icon renders
        pGuiGraphics.blit(overlayTexture, pX + 1, pY + 33, 0, 41, 8, 8)

        // start rendering key (with the key letter on them) and mouse icons, fix positioning on description and controls, set up 9 sliced key texture for wider keys

        // Action source
        drawScaledText(
            Component.literal(namespace).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC),
            pPose, pGuiGraphics, pGui, pX + 2, pY + 2, textColor, 0.8f, true
        )

        // Action Name
        drawScaledText(
            (pMode?.displayName?.copy() ?: Component.literal("???")).withStyle(ChatFormatting.BOLD),
            pPose, pGuiGraphics, pGui, pX - 1, pY + 4, textColor, 2.5f, false
        )
        // Mode Tooltip
        drawScaledText(
            (pMode?.tooltip?.copy() ?: modTranslatable(TOOL_GUN_DEF, "broken_tooltip")), pPose, pGuiGraphics, pGui,
            pX + 13, pY + 43, textColor, 0.4f, true
        )
        // KeyBinds
        pMode?.keyBinds?.forEachIndexed { index, control ->
            val moved = ((index + 1) * 12) + 2
            drawScaledText(
                toolGunBindList[control]!!.translatedKeyMessage.copy()
                    .withStyle { it.withColor(ChatFormatting.GOLD).withItalic(true) }
                    .append(control.toolGunComponent.copy().withStyle(ChatFormatting.WHITE)),
                pPose, pGuiGraphics, pGui,
                pX + 10, pY + 43 + moved, textColor, 1f, true
            )
//            toolGunBindList[control]?.key = InputConstants.getKey("key.mouse.right")
            when (toolGunBindList[control]?.key) {
                getInput("key.mouse.right") -> {
                    pPose.pushPose()
                    pPose.scaleFlat(0.68f)
                    pGuiGraphics.blit(overlayTexture, pX, pY + 69 + moved, 240, 63, 16, 16)
                    pPose.popPose()
                }

                getInput("key.mouse.middle") -> {
                    pPose.pushPose()
                    pPose.scaleFlat(0.68f)
                    pGuiGraphics.blit(overlayTexture, pX, pY + 73 + moved, 240, 47, 16, 16)
                    pPose.popPose()
                }
            }
        }
        pPose.popPose()
    }

    private fun getInput(input: String) = InputConstants.getKey(input)
}