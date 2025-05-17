package org.bread_experts_group.breadmod.client.gui.screens

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import org.bread_experts_group.breadmod.client.render.localClient

abstract class HoldScreen<T : AbstractContainerMenu>(
	menu: T,
	inventory: Inventory,
	title: Component,
	holdKey: Int
) : AbstractElementHolderScreen<T>(menu, inventory, title) {
	open val shouldClose: Boolean
		get() = !this.isKeyDown(this.getKeyCheckValue())
	open val keyCheck: KeyMapping = KeyMapping("holdScreen", holdKey, "misc")

	override fun containerTick() {
		if (this.shouldClose) this.onClose()
	}

	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {}

	fun isKeyDown(key: Int): Boolean = InputConstants.isKeyDown(localClient.window.window, key)
	fun getKeyCheckValue(): Int = this.keyCheck.key.value
}