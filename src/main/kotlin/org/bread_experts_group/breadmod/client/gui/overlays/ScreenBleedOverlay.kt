package org.bread_experts_group.breadmod.client.gui.overlays

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.util.RandomSource
import org.bread_experts_group.breadmod.client.render.localClient

class ScreenBleedOverlay : LayeredDraw.Layer {
	companion object {
		private val window = localClient.window
		private val random = RandomSource.create(404)
		private var alpha: Int = 0
		private var lastTick = 0
		var progress: Float = 0f
		var maxProgress: Float = 0f
		var active: Boolean = false
		var overrideDeathScreen: Boolean = false
		fun renderBleed(guiGraphics: GuiGraphics) {
			if (localClient.level == null || this.maxProgress == 0f || !this.active) return
			val guiTicks = localClient.gui.guiTicks
			val scaledHeight = this.window.guiScaledHeight
			val scaledWidth = this.window.guiScaledWidth
			val scaled = ((this.progress / this.maxProgress) * scaledHeight)
			val poseStack = guiGraphics.pose()

			if (this.lastTick != guiTicks) this.lastTick = guiTicks.also {
				this.alpha = this.random.nextInt(50, 85)
			}

			RenderSystem.enableBlend()
			poseStack.pushPose()
			poseStack.translate(0f, -scaledHeight + scaled, 0f)
			guiGraphics.fill(0, 0, scaledWidth, scaledHeight, 0x00FF0000 or (this.alpha shl 24))
			guiGraphics.fill(0, scaledHeight, scaledWidth, scaledHeight + 1, 0x00FFFFFF or (this.alpha shl 24))
			poseStack.popPose()
			RenderSystem.disableBlend()
		}
	}

	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		if (localClient.screen == null) Companion.renderBleed(guiGraphics)
	}
}