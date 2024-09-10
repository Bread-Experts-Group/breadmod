package bread.mod.breadmod.datagen.recipe.shapeless

import net.minecraft.data.recipes.RecipeCategory
import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateShapelessRecipeExternal(
    val name: String,
    val forItem: String,
    val category: RecipeCategory,
    val count: Int = 1,
    val neededCount: Int = 1
)