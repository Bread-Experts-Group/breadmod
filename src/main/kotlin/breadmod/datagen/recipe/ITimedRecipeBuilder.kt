package breadmod.datagen.recipe

import net.minecraft.data.recipes.RecipeBuilder

interface ITimedRecipeBuilder: RecipeBuilder {
    var timeInTicks: Int
    fun setTimeRequired(ticks: Int) = this.also { timeInTicks = ticks }
}