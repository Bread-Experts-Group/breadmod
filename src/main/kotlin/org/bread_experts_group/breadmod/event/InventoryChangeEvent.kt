package org.bread_experts_group.breadmod.event

import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.bus.api.Event
import net.neoforged.neoforge.common.NeoForge
import org.bread_experts_group.breadmod.event.InventoryChangeEvent.Companion.onInventoryChange

/**
 * Fired via [onInventoryChange] in [InventoryChangeTrigger].
 *
 * Fires when an inventory change is detected. (moving items between slots, picking up items, etc.)
 *
 * Fun little event test class, no real use at the moment.
 */
class InventoryChangeEvent(
	val player: ServerPlayer,
	val inventory: Inventory,
	val stack: ItemStack
) : Event() {
	companion object {
		@JvmStatic
		fun onInventoryChange(player: ServerPlayer, inventory: Inventory, stack: ItemStack) {
			NeoForge.EVENT_BUS.post(InventoryChangeEvent(player, inventory, stack))
		}
	}

	fun getLevel(): Level = this.player.level()

	fun getServerLevel(): ServerLevel = this.player.serverLevel()
}