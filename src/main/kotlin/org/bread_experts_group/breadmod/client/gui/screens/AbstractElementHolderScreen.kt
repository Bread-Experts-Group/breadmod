package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import org.bread_experts_group.breadmod.client.render.texture.GuiElement

abstract class AbstractElementHolderScreen<T : AbstractContainerMenu>(
	menu: T,
	inventory: Inventory,
	title: Component
) : AbstractContainerScreen<T>(menu, inventory, title) {
	// todo element holding logic, actually turn GuiElement into an abstract widget
	val elements: List<GuiElement> = buildList { this@AbstractElementHolderScreen.addGuiElements(this) }

	abstract fun addGuiElements(into: MutableList<GuiElement>)
}