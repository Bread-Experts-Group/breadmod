package org.bread_experts_group.breadmod.registry.item

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent

/**
 * Interface for [MouseScrollingEvent] and [InputEvent.MouseButton.Post]. Only runs clientside.
 */
interface IMouseItem {
	/**
	 * Fires when mouse is scrolled while holding item.
	 */
	fun onMouseScroll(scrollingEvent: MouseScrollingEvent, heldStack: ItemStack, player: Player) {}

	/**
	 * Fires during mouse input while holding item, after vanilla processing.
	 *
	 * #### You should override Item#use or Item#onEntitySwing if your only listening for left/right clicks.
	 */
	fun onMouseInputPost(mouseEvent: InputEvent.MouseButton.Post, heldStack: ItemStack, player: Player) {}

	/**
	 * Fires during mouse input while holding item, before vanilla processing.
	 *
	 * #### You should override Item#use or Item#onEntitySwing if your only listening for left/right clicks.
	 */
	fun onMouseInputPre(mouseEvent: InputEvent.MouseButton.Pre, heldStack: ItemStack, player: Player) {}
}