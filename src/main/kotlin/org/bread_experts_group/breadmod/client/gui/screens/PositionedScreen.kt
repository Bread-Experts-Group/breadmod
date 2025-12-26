package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

abstract class PositionedScreen(
	title: Component,
	val guiWidth: Int = 256,
	val guiHeight: Int = 256
) : Screen(title) {
	/**
	 * Starts at the top left of the gui and moves left to right
	 */
	var leftPos: Int = 0

	/**
	 * Starts at the top left of the gui and moves up to down
	 */
	var topPos: Int = 0

	override fun init() {
		this.leftPos = (this.width - this.guiWidth) / 2
		this.topPos = (this.height - this.guiHeight) / 2
	}
}