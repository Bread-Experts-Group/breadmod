package breadmod.compat.jei

import breadmod.ModMain
import breadmod.recipe.FluidEnergyRecipe
import mezz.jei.api.recipe.RecipeType

object ModJEIRecipeTypes {
    val fluidEnergyRecipeType: RecipeType<FluidEnergyRecipe> = RecipeType.create(ModMain.ID, "fluid_energy_recipe", FluidEnergyRecipe::class.java)
}