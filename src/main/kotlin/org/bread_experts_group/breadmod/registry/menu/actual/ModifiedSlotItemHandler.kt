package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

open class ModifiedSlotItemHandler(
	private val handler: ExpansibleItemHandler,
	index: Int,
	xPos: Int,
	yPos: Int
) : SlotItemHandler(handler, index, xPos, yPos) {
	override fun mayPickup(playerIn: Player): Boolean = !this.handler.extractItemInternal(this.index, 1).isEmpty

	override fun getItemHandler(): ExpansibleItemHandler = this.handler
}