package org.bread_experts_group.breadmod.compat.jei

import mezz.jei.api.recipe.RecipeType
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe

@Suppress("unused")
internal object ModJEIRecipeTypes {
	val DOUGH_MACHINE_RECIPE_TYPE: RecipeType<DoughMachineRecipe> =
		RecipeType.create(BreadMod.ID, "dough_machine_recipe", DoughMachineRecipe::class.java)
	val WHEAT_CRUSHER_RECIPE_TYPE: RecipeType<WheatCrusherRecipe> =
		RecipeType.create(BreadMod.ID, "wheat_crusher_recipe", WheatCrusherRecipe::class.java)
	val TOASTER_RECIPE_TYPE: RecipeType<ToasterRecipe> =
		RecipeType.create(BreadMod.ID, "toaster_recipe_category", ToasterRecipe::class.java)
}