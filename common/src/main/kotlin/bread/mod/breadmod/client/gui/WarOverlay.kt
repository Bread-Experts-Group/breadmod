package bread.mod.breadmod.client.gui

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.util.ModFonts
import bread.mod.breadmod.util.render.rgMinecraft
import bread.mod.breadmod.util.render.scaleFlat
import net.minecraft.Util
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import java.awt.Color

internal class WarOverlay : LayeredDraw.Layer {
    private val overlayTexture: ResourceLocation = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
    private var lastTick: Int = 0

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val guiTicks = rgMinecraft.gui.guiTicks
        val colorPair: Triple<Float, Float, Float> =
            if (isTimerIncreasing) Triple(0.376f, 0.91f, 0.471f)
            else if (setTimer > 0) Triple(0.922f, 0.353f, 0f)
            else Triple(0.973f, 0f, 0f)
        val millis = Util.getMillis()

        if (lastTick != guiTicks) {
            lastTick = guiTicks
            if (setTimer > 0) setTimer--
            if (increasingTimer > 0) { // Increase timer
                increasingTimer--
                timeLeft++
            } else isTimerIncreasing = false
        }

        if (timerPosition > -60.0 && !timerActive) {
            timerPosition -= Mth.clamp(millis.toFloat(), 0f, 1f) / 1.5f
        } else if (timerPosition < -1.0 && timerActive) {
            timerPosition += Mth.clamp(millis.toFloat(), 0f, 1f) / 1.5f
        }

        if (timerPosition > -60f) {
            val poseStack = guiGraphics.pose()
            val scaledWidth = rgMinecraft.window.guiScaledWidth

            poseStack.pushPose()
            poseStack.translate(scaledWidth.toDouble() / 3.3, timerPosition.toDouble(), 0.0)
            poseStack.scaleFlat(0.5f)
            guiGraphics.blit(overlayTexture, 0, 0, 0, 0, 163, 89)
            guiGraphics.blit(overlayTexture, 163, 0, 0, 90, 166, 111)

            poseStack.scaleFlat(1.7f)
            poseStack.translate(54.0, 17.0, 0.0)
            guiGraphics.setColor(colorPair.first, colorPair.second, colorPair.third, 1f)
            guiGraphics.drawString(
                rgMinecraft.font,
                Component
                    .literal(
                        "${(timeLeft / 60).toString().padStart(2, '0')}:${
                            (timeLeft % 60).toString().padStart(2, '0')
                        }"
                    )
                    .withStyle(ModFonts.WARTIMER_INFILL),
                0,
                12,
                Color.WHITE.rgb
            )
            guiGraphics.setColor(1f, 1f, 1f, 1f)
            poseStack.popPose()
        }
    }

    companion object {
        var timerPosition: Float = -60f
        var timeLeft: Int = 30
        var isTimerIncreasing: Boolean = false
        var increasingTimer: Int = 0
        var timerActive: Boolean = false
        var setTimer: Int = 0
    }
}