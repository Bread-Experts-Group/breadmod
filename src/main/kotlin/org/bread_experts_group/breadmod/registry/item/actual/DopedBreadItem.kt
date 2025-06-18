package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.ChatFormatting
import net.minecraft.ChatFormatting.GRAY
import net.minecraft.ChatFormatting.ITALIC
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.util.effectTooltip

class DopedBreadItem : Item(
	Properties()
		.food(FoodProperties.Builder().nutrition(6).alwaysEdible().build())
		.component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		.rarity(Rarity.RARE)
) {
	override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
		stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
			.forEachEffect(livingEntity::addEffect)
		return super.finishUsingItem(stack, level, livingEntity)
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		val translation = Potion.getName(potionContents.potion, "item.minecraft.potion.effect.")
		val color = PotionContents.getColor(potionContents.allEffects)
		val potionName =
			Component.literal(" [")
				.append(Component.translatable(translation))
				.append(Component.literal("]"))
				.withStyle { style -> style.withColor(color) }

		tooltipComponents.addAll(
			arrayOf(
				modTranslatable("item", "doped_bread", "tooltip").withStyle(ChatFormatting.GOLD),
				Component.empty(),
				modTranslatable("item", "doped_bread", "tooltip_two").withStyle(ITALIC, GRAY).append(potionName)
			)
		)

		potionContents.allEffects.forEach { tooltipComponents.add(effectTooltip(it, 1f, context.tickRate())) }
	}
}