package org.bread_experts_group.breadmod.registry.recipe.actual.crafting

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.crafting.CraftingBookCategory.MISC
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers

class DopedBreadRecipe : CustomRecipe(MISC) {
	override fun matches(input: CraftingInput, level: Level): Boolean {
		val count = input.ingredientCount() == 2
		var bread = false
		var potion = false
		input.items().forEach { stack ->
			if (!stack.isEmpty) {
				if (stack.`is`(Items.POTION)) {
					if (potion) return false
					potion = true
				}
				if (stack.`is`(Items.BREAD)) {
					if (bread) return false
					bread = true
				}
			}
		}
		return count && bread && potion
	}

	override fun assemble(input: CraftingInput, registries: Provider): ItemStack {
		val bread = ModItems.DOPED_BREAD.toStack()
		val potion = input.items().first { it.`is`(Items.POTION) }.copy()
		val potionContents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
		val dyeColor = DyedItemColor(PotionContents.getColor(potionContents.allEffects), false)
		bread.set(DataComponents.POTION_CONTENTS, potionContents)
		bread.set(DataComponents.DYED_COLOR, dyeColor)
		return bread
	}

	override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.BREAD_DOPING.get()
}