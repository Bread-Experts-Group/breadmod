package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class AbstractModContainerMenu(
	type : MenuType<*>,
	id : Int
) : AbstractContainerMenu(type, id) {
	fun addInventorySlots(inventory : Inventory, pX : Int, hotBarY : Int, inventoryY : Int) {
		repeat(9) { this.addSlot(Slot(inventory, it, 8 + it * 18, hotBarY)) }
		repeat(3) { y ->
			repeat(9) { x ->
				this.addSlot(
					Slot(
						inventory,
						x + y * 9 + 9,
						pX + x * 18,
						inventoryY + y * 18
					)
				)
			}
		}
	}

	override fun quickMoveStack(player : Player, index : Int) : ItemStack = this.moveStackFunction()
	override fun stillValid(player : Player) : Boolean = player.containerMenu == this
	/**
	 * ### Used in [quickMoveStack] to enable shift clicking items into the target inventory
	 * value must match the number of slots your block entity has
	 */
	open val containerSlotCount : Int = 0
	private fun moveStackFunction() : ItemStack {
		return ItemStack.EMPTY
	}
}