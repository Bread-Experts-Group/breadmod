package org.bread_experts_group.breadmod.client.gui.screens

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.localClient

abstract class HoldScreen(
	title: Component,
	holdKey: Int
) : Screen(title) {
	open val shouldClose: Boolean
		get() = !this.isKeyDown(this.getKeyCheckValue())
	open val keyCheck: KeyMapping = KeyMapping("holdScreen", holdKey, "misc")

	override fun tick() {
		if (this.shouldClose) this.onClose()
	}

	fun isKeyDown(key: Int): Boolean = InputConstants.isKeyDown(localClient.window.window, key)
	fun getKeyCheckValue(): Int = this.keyCheck.key.value
}