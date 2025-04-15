package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.localClient
import java.awt.Color

class BlueScreen : Screen(Component.empty()) {
	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.fill(0, 0, this.width, this.height, Color.BLUE.rgb)
	}

	override fun init() {
		this.addRenderableWidget(GenericButton(10, 10, 200, 16, "RESPAWN") {
			(localClient.player ?: return@GenericButton).respawn()
		})
	}

	override fun isPauseScreen(): Boolean = false
}