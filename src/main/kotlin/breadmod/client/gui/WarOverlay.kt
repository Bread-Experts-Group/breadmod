package breadmod.client.gui

import breadmod.ModMain.modLocation
import breadmod.client.gui.WarTickerClient.timerActive
import breadmod.util.ModFonts
import breadmod.util.render.rgMinecraft
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraftforge.client.gui.overlay.ForgeGui
import java.awt.Color

class WarOverlay : AbstractModGuiOverlay() {
    private val overlayTexture = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
    var timerPosition = -110f
    var lastTimerPosition = -110f

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
        val seconds = WarTickerClient.timeLeft % 60
        val minutes = WarTickerClient.timeLeft / 60
        val formattedSeconds = if (seconds < 10) "0$seconds" else seconds
        val formattedMinutes = if (minutes < 10) "0$minutes" else minutes
        val colorPair: Triple<Float, Float, Float> =
            if (WarTickerClient.isTimerIncreasing) Triple(0.376f, 0.91f, 0.471f)
            else Triple(0.973f, 0f, 0f)
        val millis = Util.getMillis()

        if (timerPosition > -110.0 && !timerActive) {
            timerPosition -= Mth.clamp(millis / 2700f, 0f, 1f)
        } else if (timerPosition < -1.0 && timerActive) {
            timerPosition += Mth.clamp(millis / 2700f, 0f, 1f)
        }

        if (timerPosition > -110f) {
            pPoseStack.pushPose()
            pPoseStack.scaleFlat(0.5f)
            pPoseStack.translate(0.0, timerPosition.toDouble(), 0.0)
            pPoseStack.translate(pScreenWidth - (pScreenWidth / 2).toDouble(), 0.0, 0.0)
            pGuiGraphics.blit(overlayTexture, 0, 0, 0, 0, 163, 89)
            pGuiGraphics.blit(overlayTexture, 163, 0, 0, 90, 166, 111)

            pPoseStack.scaleFlat(1.3f)
            pPoseStack.translate(60.0, 0.0, 0.0)
            pGuiGraphics.setColor(colorPair.first, colorPair.second, colorPair.third, 1f)
            pGuiGraphics.drawString(
                rgMinecraft.font,
                Component.literal("$formattedMinutes:$formattedSeconds").withStyle(ModFonts.WARTIMER_INFILL),
                0,
                12,
                Color.WHITE.rgb
            )
            pGuiGraphics.setColor(1f, 1f, 1f, 1f)
            pPoseStack.popPose()
        }
    }
}