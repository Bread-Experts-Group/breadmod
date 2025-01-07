package org.bread_experts_group.breadmod.util.handlers

import net.neoforged.neoforge.items.ItemStackHandler

/**
 * Simple [ItemStackHandler] extension.
 */
class ExtendedItemStackHandler(slots : Int) : ItemStackHandler(slots) {
	constructor() : this(1)
	/**
	 * @return True if each slot is empty.
	 */
	fun isEmpty() : Boolean = this.stacks.all { it.isEmpty }
	/**
	 * @return True if any slot is not empty.
	 */
	fun isNotEmpty() : Boolean = this.stacks.any { !it.isEmpty }
	fun filledSlots() : Int = this.stacks.filter { !it.isEmpty }.size
	fun emptySlots() : Int = this.stacks.filter { it.isEmpty }.size
}