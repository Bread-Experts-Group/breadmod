package breadmod.compat.jei

import breadmod.ModMain
import breadmod.recipe.FluidEnergyRecipe
import breadmod.recipe.WheatCrusherRecipe
import mezz.jei.api.recipe.RecipeType

object ModJEIRecipeTypes {
    val fluidEnergyRecipeType: RecipeType<FluidEnergyRecipe> = RecipeType.create(ModMain.ID, "fluid_energy_recipe", FluidEnergyRecipe::class.java)
    val wheatCrusherRecipeType: RecipeType<WheatCrusherRecipe> = RecipeType.create(ModMain.ID, "wheat_crusher_recipe", WheatCrusherRecipe::class.java)
}