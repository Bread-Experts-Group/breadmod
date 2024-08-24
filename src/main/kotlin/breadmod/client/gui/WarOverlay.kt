package breadmod.client.gui

import breadmod.ModMain.modLocation
import breadmod.client.gui.WarTicker.active
import breadmod.util.ModFonts
import breadmod.util.render.minecraft
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.overlay.ForgeGui
import java.awt.Color

class WarOverlay: AbstractModGuiOverlay() {
    private val overlayTexture = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
    private var timerPos = -110.0

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
        val seconds = WarTicker.timer % 60
        val minutes = WarTicker.timer / 60
        val formattedSeconds = if (seconds < 10) "0$seconds" else seconds
        val formattedMinutes = if (minutes < 10) "0$minutes" else minutes
        val colorPair: Triple<Float, Float, Float> =
            if (WarTicker.isIncreasing) Triple(0.376f,0.91f,0.471f)
            else Triple(0.973f, 0f, 0f)

        pPoseStack.pushPose()
        pPoseStack.scaleFlat(0.5f)
        if (timerPos > -110.0 && !active) { // todo somehow needs to be capped at 60 updates per second
            timerPos --
        } else if (timerPos < 0.0 && active) {
            timerPos ++
        }
        pPoseStack.translate(0.0, timerPos, 0.0)
        pPoseStack.translate(pScreenWidth - (pScreenWidth / 2).toDouble(), 0.0, 0.0)
        pGuiGraphics.blit(overlayTexture, 0, 0, 0, 0, 163, 89)
        pGuiGraphics.blit(overlayTexture, 163, 0, 0, 90, 166, 111)

        pPoseStack.scaleFlat(1.3f)
        pPoseStack.translate(60.0, 0.0, 0.0)
        pGuiGraphics.setColor(colorPair.first, colorPair.second, colorPair.third, 1f)
        pGuiGraphics.drawString(
            minecraft.font,
            Component.literal("$formattedMinutes:$formattedSeconds").withStyle(ModFonts.WARTIMER_INFILL),
            0,
            12,
            Color.WHITE.rgb
        )
        pGuiGraphics.setColor(1f, 1f, 1f, 1f)
        pPoseStack.popPose()
    }
}