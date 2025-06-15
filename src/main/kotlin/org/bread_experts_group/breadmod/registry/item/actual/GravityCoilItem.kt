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

class GravityCoilItem : Item(
	Properties()
		.stacksTo(1)
		.attributes(
			ItemAttributeModifiers.builder()
				.add(
					Attributes.GRAVITY,
					AttributeModifier(BreadMod.modLocation("gravity"), -0.06, ADD_VALUE),
					HAND
				).build()
		)
), EquipmentSlotListener {
	override fun onItemEquipped(stack: ItemStack, entity: Entity, level: Level, equipmentSlot: EquipmentSlot) {
		level.playSound(null, entity.x, entity.y, entity.z, ModSounds.GRAVITY_COIL, AMBIENT, 1f, 1f)
	}

	override fun getName(stack: ItemStack): Component =
		super.getName(stack).copy().withStyle { style -> style.withColor(color(23, 138, 252)) }
}