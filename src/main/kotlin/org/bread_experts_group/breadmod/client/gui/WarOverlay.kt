package org.bread_experts_group.breadmod.client.gui

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations.WAR_TIMER
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.ModFonts
import java.awt.Color

internal class WarOverlay : LayeredDraw.Layer {
	private var lastTick: Int = 0
	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		val guiTicks = localClient.gui.guiTicks
		val colorPair: Triple<Float, Float, Float> =
			if (Companion.isTimerIncreasing) Triple(0.376f, 0.91f, 0.471f)
			else if (Companion.setTimer > 0) Triple(0.922f, 0.353f, 0f)
			else Triple(0.973f, 0f, 0f)

		if (this.lastTick != guiTicks) {
			this.lastTick = guiTicks
			if (Companion.setTimer > 0) Companion.setTimer--
			if (Companion.increasingTimer > 0) { // Increase timer
				Companion.increasingTimer--
				Companion.timeLeft++
			} else Companion.isTimerIncreasing = false
		}

		if (Companion.timerPosition > -60.0 && !Companion.timerActive) {
			Companion.timerPosition -= 4f * deltaTracker.gameTimeDeltaTicks
		} else if (Companion.timerPosition < -1.0 && Companion.timerActive) {
			Companion.timerPosition += 4f * deltaTracker.gameTimeDeltaTicks
		}

		if (Companion.timerPosition > -60f) {
			val poseStack = guiGraphics.pose()
			val scaledWidth = localClient.window.guiScaledWidth

			poseStack.pushPose()
			poseStack.translate((scaledWidth.toDouble() - 200) / 2, Companion.timerPosition.toDouble(), 0.0)
			poseStack.scaleFlat(0.5f)
			WAR_TIMER.blitTexture(guiGraphics, 0, 0)
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
		var timerPosition: Float = -60f
		var timeLeft: Int = 30
		var isTimerIncreasing: Boolean = false
		var increasingTimer: Int = 0
		var timerActive: Boolean = false
		var setTimer: Int = 0
	}
}