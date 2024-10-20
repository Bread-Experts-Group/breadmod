package breadmod.client.jei

import breadmod.ModMain
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.recipe.fluidEnergy.ToasterRecipe
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import mezz.jei.api.recipe.RecipeType

object ModJEIRecipeTypes {
    val doughMachineRecipeType: RecipeType<DoughMachineRecipe> =
        RecipeType.create(ModMain.ID, "dough_machine_recipe", DoughMachineRecipe::class.java)
    val wheatCrusherRecipeType: RecipeType<WheatCrushingRecipe> =
        RecipeType.create(ModMain.ID, "wheat_crusher_recipe", WheatCrushingRecipe::class.java)
    val toasterRecipeType: RecipeType<ToasterRecipe> =
        RecipeType.create(ModMain.ID, "toaster_recipe_category", ToasterRecipe::class.java)
}