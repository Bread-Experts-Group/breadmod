package org.bread_experts_group.breadmod.registry.item

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Equipable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

/**
 * Interface for listening to equipment slot changes. (switching items in hand and armor slots)
 *
 * [Equipable] exists, but you can only specify one [EquipmentSlot] for it's getEquipmentSlot method.
 * This interface allows for multiple equipment slots (and also allows for firing code when unequipping an item).
 *
 * @see Equipable
 */
interface EquipmentSlotListener {
	/**
	 * Fired when this item is equipped.
	 */
	fun onItemEquipped(stack: ItemStack, entity: Entity, level: Level, equipmentSlot: EquipmentSlot) {}

	/**
	 * Fired when this item is unequipped.
	 */
	fun onItemUnequipped(stack: ItemStack, entity: Entity, level: Level, equipmentSlot: EquipmentSlot) {}

	/**
	 * @param oldStack This item before being changed to the [newStack].
	 * @param newStack The new item being equipped.
	 */
	fun onEquipmentChange(
		oldStack: ItemStack,
		newStack: ItemStack,
		entity: Entity,
		level: Level,
		equipmentSlot: EquipmentSlot
	) {
	}
}