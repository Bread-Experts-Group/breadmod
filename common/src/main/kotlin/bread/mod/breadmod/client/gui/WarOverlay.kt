package bread.mod.breadmod.client.gui

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.util.ModFonts
import bread.mod.breadmod.util.render.rgMinecraft
import bread.mod.breadmod.util.render.scaleFlat
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import java.awt.Color

open class WarOverlay: AbstractModGuiOverlay() {
    private val overlayTexture = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
    var timerPosition = -110f
//    var lastTimerPosition = -110f
    var lastTick = 0
    
    override fun renderOverlay(
        guiGraphics: GuiGraphics,
        partialTick: Float,
        deltaTracker: DeltaTracker,
        screenWidth: Int,
        screenHeight: Int,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        player: LocalPlayer
    ) {
        val guiTicks = rgMinecraft.gui.guiTicks
        val seconds = timeLeft % 60
        val minutes = timeLeft / 60
        val formattedSeconds = if (seconds < 10) "0$seconds" else seconds
        val formattedMinutes = if (minutes < 10) "0$minutes" else minutes
        val colorPair: Triple<Float, Float, Float> =
            if (isTimerIncreasing) Triple(0.376f, 0.91f, 0.471f)
            else Triple(0.973f, 0f, 0f)
        val millis = Util.getMillis()

        if (lastTick != guiTicks) {
            lastTick = guiTicks
            if (increasingTimer > 0) { // Increase timer
                increasingTimer--
                timeLeft++
            } else isTimerIncreasing = false
        }

        if (timerPosition > -110.0 && !timerActive) {
            timerPosition -= Mth.clamp(millis / 2700f, 0f, 1f)
        } else if (timerPosition < -1.0 && timerActive) {
            timerPosition += Mth.clamp(millis / 2700f, 0f, 1f)
        }

        if (timerPosition > -110f) {
            poseStack.pushPose()
            poseStack.scaleFlat(0.5f)
            poseStack.translate(0.0, timerPosition.toDouble(), 0.0)
            poseStack.translate(screenWidth - (screenWidth / 2).toDouble(), 0.0, 0.0)
            guiGraphics.blit(overlayTexture, 0, 0, 0, 0, 163, 89)
            guiGraphics.blit(overlayTexture, 163, 0, 0, 90, 166, 111)

            poseStack.scaleFlat(1.7f)
            poseStack.translate(54.0, 17.0, 0.0)
            guiGraphics.setColor(colorPair.first, colorPair.second, colorPair.third, 1f)
            guiGraphics.drawString(
                rgMinecraft.font,
                Component.literal("$formattedMinutes:$formattedSeconds").withStyle(ModFonts.WARTIMER_INFILL),
                0,
                12,
                Color.WHITE.rgb
            )
            guiGraphics.setColor(1f, 1f, 1f, 1f)
            poseStack.popPose()
        }
    }

    companion object {
        var timeLeft = 30
        var isTimerIncreasing = false
        var increasingTimer = 0
        var timerActive = false
    }
}