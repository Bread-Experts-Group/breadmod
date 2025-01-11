package org.bread_experts_group.breadmod.registry.item

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.event.InputEvent

/**
 * Interface for [InputEvent.Key]. Only runs clientside.
 */
interface IKeyboardItem {
	/**
	 * Fires when a key is pressed.
	 */
	fun onKeyboardPress(keyEvent: InputEvent.Key, heldStack: ItemStack, player: Player)
}