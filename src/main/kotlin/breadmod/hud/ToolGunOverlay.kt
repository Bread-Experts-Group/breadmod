package breadmod.hud

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.ToolGunItem
import breadmod.item.ToolGunItem.Companion.MODE_NAMESPACE_TAG
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay
import java.awt.Color

class ToolGunOverlay: IGuiOverlay {
    private val overlayTexture = modLocation("textures", "gui", "hud", "tool_gun_overlay.png")
    private val textColor = Color.WHITE.rgb

    override fun render(
        pGui: ForgeGui,
        pGuiGraphics: GuiGraphics,
        pPartialTick: Float,
        pScreenWidth: Int,
        pScreenHeight: Int
    ) {
        val pose = pGuiGraphics.pose()
        val x = pScreenWidth - (pScreenWidth - 3)
        val y = pScreenHeight - (pScreenHeight - 3)
        val player = pGui.minecraft.player
        val heldStack = player?.getItemInHand(InteractionHand.MAIN_HAND) ?: return

        val item = heldStack.item
        if(!pGui.minecraft.options.hideGui && item is ToolGunItem) {
            pGui.setupOverlayRenderState(true, false)
            renderBackground(pGuiGraphics, pose, x, y)

            val ensured = item.ensureCurrentMode(heldStack)
            renderMode(
                ensured.getString(MODE_NAMESPACE_TAG), item.getCurrentMode(heldStack),
                pGuiGraphics, pose, pGui, x, y
            )
        }
    }

    private fun renderBackground(pGuiGraphics: GuiGraphics, pPose: PoseStack, x: Int, y: Int) {
        pPose.pushPose()
        // Main Background Texture
        pGuiGraphics.blit(overlayTexture, x, y, 0, 0, 166, 41)
        pPose.popPose()
    }

    private fun renderMode(namespace: String, pMode: ModToolGunModeDataLoader.ToolgunMode, pGuiGraphics: GuiGraphics, pPose: PoseStack, pGui: ForgeGui, pX: Int, pY: Int) {
        pPose.pushPose()
        // Icon renders
        pGuiGraphics.blit(overlayTexture, pX + 1, pY + 33, 0, 41, 8, 8)

        // Action source
        drawScaledText(Component.literal(namespace).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC),
            pPose, pGuiGraphics, pGui, pX + 2, pY + 2, 0.8f, true
        )

        // Action Name
        drawScaledText(
            pMode.displayName.copy().withStyle(ChatFormatting.BOLD),
            pPose, pGuiGraphics, pGui, pX - 1, pY + 4, 2.5f, false
        )
        // Mode Tooltip
        drawScaledText(pMode.tooltip.copy(), pPose, pGuiGraphics, pGui,
            pX + 13, pY + 43, 0.4f, true
        )
        // Keybinds
        pMode.keyBinds.forEachIndexed { index, control ->
            val moved = ((index+1) * 9) + 3
            drawScaledText(
                toolGunBindList[control]!!.translatedKeyMessage.copy()
                    .withStyle { it.withColor(ChatFormatting.GOLD).withItalic(true) }
                    .append(control.toolGunComponent.copy().withStyle(ChatFormatting.WHITE)),
                pPose, pGuiGraphics, pGui,
                pX + 73, pY + 43 + moved, 1f, true
            )
        }
        pPose.popPose()
    }

    private fun drawScaledText(pText: Component, pPose: PoseStack, pGuiGraphics: GuiGraphics, pGui: ForgeGui, pX: Int, pY: Int, pScale: Float, pDropShadow: Boolean) {
        pPose.scaleFlat(pScale)
        pGuiGraphics.drawString(
            pGui.minecraft.font,
            pText,
            pX,
            pY,
            textColor,
            pDropShadow
        )
        pPose.scaleFlat(1f)
    }

    private fun PoseStack.scaleFlat(scale: Float) = this.scale(scale, scale, scale)
}