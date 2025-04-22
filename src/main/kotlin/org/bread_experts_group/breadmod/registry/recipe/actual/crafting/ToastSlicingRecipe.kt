package org.bread_experts_group.breadmod.registry.recipe.actual.crafting

import net.minecraft.tags.ItemTags
import net.minecraft.world.item.crafting.RecipeSerializer
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers

class ToastSlicingRecipe : AbstractCuttingRecipe(
	ModItems.TOASTED_BREAD.get().defaultInstance,
	ModItems.TOAST_SLICE.toStack(),
	8,
	ItemTags.SWORDS
) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.TOAST_SLICE.get()
}