package org.bread_experts_group.breadmod.registry.recipe.actual.crafting

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.crafting.CraftingBookCategory.MISC
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.registry.item.actual.armor.BreadArmorItem
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers

class BreadArmorPotionRecipe : CustomRecipe(MISC) {
	override fun matches(input: CraftingInput, level: Level): Boolean {
		val count = input.ingredientCount() == 2
		var armor = false
		var potion = false
		input.items().forEach { stack ->
			if (!stack.isEmpty) when (stack.item) {
				is PotionItem     -> {
					if (potion) return false
					potion = true
				}
				is BreadArmorItem -> {
					if (armor) return false
					armor = true
				}
			}
		}
		return count && armor && potion
	}

	override fun assemble(input: CraftingInput, registries: Provider): ItemStack {
		val armor = input.items().first { it.item is BreadArmorItem }.copy()
		val potion = input.items().first { it.item is PotionItem }.copy()
		val potionContents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		val dyeColor = DyedItemColor(PotionContents.getColor(potionContents.allEffects), false)
		armor.set(DataComponents.POTION_CONTENTS, potionContents)
		armor.set(DataComponents.DYED_COLOR, dyeColor)
		armor.set(DataComponents.RARITY, Rarity.UNCOMMON)
		return armor
	}

	override fun getGroup(): String = "bread_armor"

	override fun getRemainingItems(input: CraftingInput): NonNullList<ItemStack> {
		val nonNullList = NonNullList.withSize(input.size(), ItemStack.EMPTY)

		for (i: Int in nonNullList.indices) {
			val stack = input.getItem(i)
			if (stack.`is`(Items.POTION)) {
				nonNullList[i] = Items.GLASS_BOTTLE.defaultInstance
			} else nonNullList[i] = stack.craftingRemainingItem
		}

		return nonNullList
	}

	override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.ARMOR_POTION.get()
}