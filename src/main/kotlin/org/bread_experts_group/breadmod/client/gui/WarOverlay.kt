package org.bread_experts_group.breadmod.client.gui

import net.minecraft.Util
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.ModFonts
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

internal class WarOverlay : LayeredDraw.Layer {
	private val overlayTexture : ResourceLocation = modLocation("textures", "gui", "hud", "war_overlay_timer.png")
	private var lastTick : Int = 0
	override fun render(guiGraphics : GuiGraphics, deltaTracker : DeltaTracker) {
		val guiTicks = localClient.gui.guiTicks
		val colorPair : Triple<Float, Float, Float> =
			if (Companion.isTimerIncreasing) Triple(0.376f, 0.91f, 0.471f)
			else if (Companion.setTimer > 0) Triple(0.922f, 0.353f, 0f)
			else Triple(0.973f, 0f, 0f)
		val millis = Util.getMillis()

		if (this.lastTick != guiTicks) {
			this.lastTick = guiTicks
			if (Companion.setTimer > 0) Companion.setTimer--
			if (Companion.increasingTimer > 0) { // Increase timer
				Companion.increasingTimer--
				Companion.timeLeft++
			} else Companion.isTimerIncreasing = false
		}

		if (Companion.timerPosition > -60.0 && !Companion.timerActive) {
			Companion.timerPosition -= Mth.clamp(millis.toFloat(), 0f, 1f) / 1.5f
		} else if (Companion.timerPosition < -1.0 && Companion.timerActive) {
			Companion.timerPosition += Mth.clamp(millis.toFloat(), 0f, 1f) / 1.5f
		}

		if (Companion.timerPosition > -60f) {
			val poseStack = guiGraphics.pose()
			val scaledWidth = localClient.window.guiScaledWidth

			poseStack.pushPose()
			poseStack.translate(scaledWidth.toDouble() / 3.3, Companion.timerPosition.toDouble(), 0.0)
			poseStack.scaleFlat(0.5f)
			guiGraphics.blit(this.overlayTexture, 0, 0, 0, 0, 163, 89)
			guiGraphics.blit(this.overlayTexture, 163, 0, 0, 90, 166, 111)

			poseStack.scaleFlat(1.7f)
			poseStack.translate(54.0, 17.0, 0.0)
			guiGraphics.setColor(colorPair.first, colorPair.second, colorPair.third, 1f)
			guiGraphics.drawString(
				localClient.font,
				Component
					.literal(
						"${(Companion.timeLeft / 60).toString().padStart(2, '0')}:${
							(Companion.timeLeft % 60).toString().padStart(2, '0')
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
		var timerPosition : Float = -60f
		var timeLeft : Int = 30
		var isTimerIncreasing : Boolean = false
		var increasingTimer : Int = 0
		var timerActive : Boolean = false
		var setTimer : Int = 0
	}
}