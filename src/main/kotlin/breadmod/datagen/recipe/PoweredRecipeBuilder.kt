package breadmod.datagen.recipe

import net.minecraft.data.recipes.RecipeBuilder

interface PoweredRecipeBuilder: RecipeBuilder {
    var powerInRF: Int
    fun setRFRequired(rf: Int) = this.also { powerInRF = rf }
}