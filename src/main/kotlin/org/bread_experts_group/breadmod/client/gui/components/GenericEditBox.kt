package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.localClient

class GenericEditBox(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	message: Component,
	init: (EditBox) -> Unit = {},
	private val onKeyPressed: (EditBox, Int) -> Unit
) : EditBox(localClient.font, x, y, width, height, message) {
	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		this.onKeyPressed(this, keyCode)
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	init {
		init(this)
	}
}