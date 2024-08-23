package breadmod.client.gui

import breadmod.ModMain.modLocation
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraftforge.client.gui.overlay.ForgeGui

class WarOverlay: AbstractModGuiOverlay() {
    private val overlayTexture = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
    private val overlayTextureNumbers = modLocation("textures", "gui", "hud", "war_overlay_numbers.png")

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
        pPoseStack.pushPose()
        pPoseStack.scaleFlat(0.5f)
        pPoseStack.translate(pScreenWidth - (pScreenWidth / 2).toDouble(), 0.0, 0.0)
        pGuiGraphics.blit(overlayTexture, 0, 0, 0, 0, 163, 89)
        pGuiGraphics.blit(overlayTexture, 163, 0, 0, 90, 166, 111)

        pPoseStack.scaleFlat(1.3f)
        pGuiGraphics.setColor(0.973f, 0f, 0f, 1f)
        pGuiGraphics.blit(overlayTextureNumbers, 70, 12, 0, 41, 34, 41)
        pGuiGraphics.blit(overlayTextureNumbers, 105, 12, 0, 41, 34, 41)
        pGuiGraphics.blit(overlayTextureNumbers, 158, 12, 0, 41, 34, 41)
        pGuiGraphics.blit(overlayTextureNumbers, 193, 12, 0, 41, 34, 41)
        pGuiGraphics.setColor(1f, 1f, 1f, 1f)
        pPoseStack.popPose()
    }
}