package org.bread_experts_group.breadmod.registry.item.actual.armor

import net.minecraft.ChatFormatting.GRAY
import net.minecraft.ChatFormatting.ITALIC
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.ModConfiguration.COMMON
import org.bread_experts_group.breadmod.util.effectTooltip
import java.awt.Color
import java.lang.Math.clamp
import java.text.DecimalFormat
import kotlin.random.Random

class BreadArmorItem(type: Type) : ArmorItem(
	ModArmorMaterials.BREAD,
	type,
	Properties()
		.component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		.component(DataComponents.DYED_COLOR, DyedItemColor(Color(216, 196, 170).rgb, false))
		.stacksTo(1)
) {
	companion object {
		val decimalFormat: DecimalFormat = DecimalFormat("0.#")
	}

	// todo finish the tooltip for this
	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		if (potionContents != PotionContents.EMPTY) {
			val translation = Potion.getName(potionContents.potion, "item.minecraft.potion.effect.")
			val color = PotionContents.getColor(potionContents.allEffects)
			val potionName =
				Component.literal(" [")
					.append(Component.translatable(translation))
					.append(Component.literal("]"))
					.withStyle { style -> style.withColor(color) }
			tooltipComponents.add(
				modTranslatable("item", "bread_armor", "tooltip").withStyle(ITALIC, GRAY).append(potionName)
			)
		}
		val rangeMulti = COMMON.dopedArmorEffectDistanceMultiplier.get()
		potionContents.allEffects.forEach { instance ->
			val initialTag = effectTooltip(instance, 1f, context.tickRate())
			val range = rangeMulti * clamp(instance.amplifier.toLong(), 1, 99)
			tooltipComponents.add(
				initialTag.append(" ").append(
					modTranslatable(
						"item", "bread_armor", "range",
						args = listOf(Companion.decimalFormat.format(range), if (range == 1.0) "block" else "blocks")
					).withStyle(instance.effect.value().category.tooltipFormatting)
				)
			)
		}
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		if (entity !is LivingEntity || !entity.armorSlots.contains(stack)) return

		if (entity.isInLava) stack.hurtAndBreak(Int.MAX_VALUE, entity, this.type.slot)
		val decayChance = COMMON.breadArmorDecayChancePerTick.get()
		if (Random.nextInt(1, decayChance) == 1 || entity.isInWaterOrRain)
			stack.hurtAndBreak(if (entity.isUnderWater) 2 else 1, entity, this.type.slot)
		val distanceMultiplier = COMMON.dopedArmorEffectDistanceMultiplier.get()
		val potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		potion.allEffects.forEach { instance ->
			val distance = distanceMultiplier * instance.amplifier
			val effectArea = AABB.ofSize(entity.eyePosition, distance, distance, distance)
			level.getEntities(null, effectArea).forEach {
				if (it is LivingEntity) it.addEffect(
					MobEffectInstance(instance),
					entity
				)
			}
		}

		if (entity is Player) {
			val breadArmorEquipped = entity.armorSlots.count { stack.item is BreadArmorItem }
			if (entity.foodData.foodLevel < breadArmorEquipped) entity.foodData.foodLevel = breadArmorEquipped * 2
		}
	}
}