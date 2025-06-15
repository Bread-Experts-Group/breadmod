package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundSource.AMBIENT
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup.HAND
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.item.EquipmentSlotListener
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.Color.color

class SpeedCoilItem : Item(
	Properties()
		.stacksTo(1)
		.attributes(
			ItemAttributeModifiers.builder()
				.add(
					Attributes.MOVEMENT_SPEED,
					AttributeModifier(BreadMod.modLocation("coil_speed"), 0.15, ADD_VALUE),
					HAND
				).build()
		)
), EquipmentSlotListener {
	override fun onItemEquipped(stack: ItemStack, entity: Entity, level: Level, equipmentSlot: EquipmentSlot) {
		level.playSound(null, entity.x, entity.y, entity.z, ModSounds.SPEED_COIL, AMBIENT, 1f, 1f)
	}

	override fun getName(stack: ItemStack): Component =
		super.getName(stack).copy().withStyle { style -> style.withColor(color(252, 34, 23)) }
}