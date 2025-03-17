package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color

class BlueScreen : Screen(Component.empty()) {
	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.fill(0, 0, this.width, this.height, Color.BLUE.rgb)
	}

	override fun isPauseScreen(): Boolean = false
}