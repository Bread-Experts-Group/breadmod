package breadmod.hud

import breadmod.ModMain.modLocation
import breadmod.item.ToolGunItem
import breadmod.registry.item.ModItems
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay
import java.awt.Color

class ToolGunOverlay: IGuiOverlay {
    private val overlayTexture = modLocation("textures", "gui", "hud", "tool_gun_overlay.png")

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
        val holdingToolGun = player?.isHolding(ModItems.TOOL_GUN.get())
        val toolGunMode = ToolGunItem.currentMode


        if(!pGui.minecraft.options.hideGui && holdingToolGun == true) {

            pGui.setupOverlayRenderState(true, false)
            renderBackground(pGuiGraphics, pose, x, y)

            when(toolGunMode) {
                ToolGunItem.ToolGunModes.REMOVER -> renderRemoverMode(pGuiGraphics, pose, pGui, x, y)
                ToolGunItem.ToolGunModes.CREATOR -> renderCreatorMode(pGuiGraphics, pose, pGui, x, y)
            }
        }
    }

    private fun renderBackground(pGuiGraphics: GuiGraphics, pPose: PoseStack, x: Int, y: Int) {
        pPose.pushPose()
        // Main Background Texture
        pGuiGraphics.blit(overlayTexture, x, y, 0, 0, 148, 42)
        pPose.popPose()
    }

    private fun renderRemoverMode(pGuiGraphics: GuiGraphics, pPose: PoseStack, pGui: ForgeGui, x: Int, y: Int) {
        pPose.pushPose()
        pPose.scale(3.0f, 3.0f, 3.0f)
        pGuiGraphics.drawString(
            pGui.minecraft.font,
            Component.literal("Remover").withStyle(ChatFormatting.BOLD),
            x - 1,
            y,
            Color(255,255,255).rgb,
            false
        )
        pPose.popPose()
        pPose.pushPose()
        // Info icon
        pGuiGraphics.blit(overlayTexture, x, y + 32, 148, 0, 8, 8)
        pPose.scale(0.8f, 0.8f, 0.8f)
        pGuiGraphics.drawString(
            pGui.minecraft.font,
            Component.literal("Remove entities with right click."),
            x + 12,
            y + 43,
            Color(255,255,255).rgb,
            false
        )
        pPose.popPose()
    }

    private fun renderCreatorMode(pGuiGraphics: GuiGraphics, pPose: PoseStack, pGui: ForgeGui, x: Int, y: Int) {
        pPose.pushPose()
        pPose.scale(3.0f, 3.0f, 3.0f)
        pGuiGraphics.drawString(
            pGui.minecraft.font,
            Component.literal("Creator").withStyle(ChatFormatting.BOLD),
            x - 1,
            y,
            Color(255,255,255).rgb,
            false
        )
        pPose.popPose()
        pPose.pushPose()
        // Info icon
        pGuiGraphics.blit(overlayTexture, x, y + 32, 148, 0, 8, 8)
        pPose.scale(0.8f, 0.8f, 0.8f)
        pGuiGraphics.drawString(
            pGui.minecraft.font,
            Component.literal("Add entities/blocks with right click."),
            x + 12,
            y + 43,
            Color(255,255,255).rgb,
            false
        )
        pPose.popPose()
    }

    private fun printOverlayStats(pScreenWidth: Int, pScreenHeight: Int, x: Int, y: Int) {
        println("-----------------")
        println("Overlay Stats")
        println("screen width: $pScreenWidth")
        println("screen height: $pScreenHeight")
        println("x value: $x")
        println("y value: $y")
        println("-----------------")
    }
}