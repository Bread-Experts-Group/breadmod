package org.bread_experts_group.breadmod.client.gui.overlays

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.client.gui.screens.ChatScreen
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import java.lang.Math.clamp

class InternetChatRelayOverlay : LayeredDraw.Layer {
	private var setDelta = false // Hacky solution to reset the delta with animation progress
	private var animationProgress = 0.0
		set(value) {
			val deltaFlip = if (this.shouldShow) this.animationDelta else -this.animationDelta
			field = clamp(value + deltaFlip, if (this.showEdge) 5.5 else 0.0, 211.0)
		}
	private var animationDelta = 0.0
		set(value) {
			field = clamp(value, 0.0, 3.0)
		}
	private var shouldShow = false // Should only be true when the ChatScreen is active
	private var showEdge = false // Should only be true when an IRC connection is active
	private val texture = modLocation("textures", "gui", "hud", "irc_overlay.png")

	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		val currentScreen = localClient.screen
		val flag = currentScreen is ChatScreen
		val scaledHeight = localClient.window.guiScaledHeight.toDouble()
		val scaledWidth = localClient.window.guiScaledWidth.toDouble()
		val poseStack = guiGraphics.pose()

//		this.showEdge = true
//		this.shouldShow = flag

		if (this.shouldShow) {
			if (this.animationProgress == 5.5 || this.animationProgress == 0.0) this.animationDelta = 0.0
			if (this.animationProgress == 211.0) this.setDelta = true
			this.animationProgress++
			this.animationDelta += 0.2 * deltaTracker.gameTimeDeltaTicks
		} else if (!flag) {
			if (this.setDelta) {
				this.animationDelta = 0.0
				this.setDelta = false
			}
			this.animationProgress--
			this.animationDelta += 0.2 * deltaTracker.gameTimeDeltaTicks
		}

		if ((flag || this.animationProgress > 0.0) || this.showEdge) {
			poseStack.pushPose()
			poseStack.translate((scaledWidth - this.animationProgress), (scaledHeight - 160), 0.0)
			poseStack.scaleFlat(1.2f)
			guiGraphics.blit(this.texture, 0, 0, 0f, 0f, 176, 117, 256, 256)
			poseStack.popPose()
		}
	}
}