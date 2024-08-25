package breadmod.datagen.recipe

import net.minecraft.data.recipes.RecipeBuilder

interface IPoweredRecipeBuilder : RecipeBuilder {
    var powerInRF: Int
    fun setRFRequired(rf: Int) = this.also { powerInRF = rf }
}