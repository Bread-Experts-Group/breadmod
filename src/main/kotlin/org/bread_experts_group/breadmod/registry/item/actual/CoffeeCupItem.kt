package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.component.DyedItemColor
import org.bread_experts_group.breadmod.util.Color

class CoffeeCupItem : Item(
	Properties()
		.food(FoodProperties.Builder().alwaysEdible().nutrition(1).build())
		.component(DataComponents.DYED_COLOR, DyedItemColor(Color.BLUE, false))
) {
	override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.DRINK
}