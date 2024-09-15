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

// todo overlay position is messed up in production
// todo overlay numbers are not padded in production (no 0 in front of timer positions under 10)
open class WarOverlay : LayeredDraw.Layer {
    val overlayTexture: ResourceLocation = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
    var timerPosition: Float = -110f
    var lastTick: Int = 0
    //    var lastTimerPosition = -110f

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val guiTicks = rgMinecraft.gui.guiTicks
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
            val poseStack = guiGraphics.pose()
            val screenWidth = rgMinecraft.window.screenWidth

            poseStack.pushPose()
            poseStack.scaleFlat(0.5f)
            poseStack.translate(0.0, timerPosition.toDouble(), 3600.0)
            poseStack.translate(screenWidth - (screenWidth / 2).toDouble(), 0.0, 0.0)
            guiGraphics.blit(overlayTexture, 0, 0, 0, 0, 163, 89)
            guiGraphics.blit(overlayTexture, 163, 0, 0, 90, 166, 111)

            poseStack.scaleFlat(1.7f)
            poseStack.translate(54.0, 17.0, 0.0)
            guiGraphics.setColor(colorPair.first, colorPair.second, colorPair.third, 1f)
            guiGraphics.drawString(
                rgMinecraft.font,
                Component
                    .literal("${(timeLeft / 60).toString().padStart(2)}:${(timeLeft % 60).toString().padStart(2)}")
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
        var timeLeft: Int = 30
        var isTimerIncreasing: Boolean = false
        var increasingTimer: Int = 0
        var timerActive: Boolean = false
    }
}