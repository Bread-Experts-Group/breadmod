package bread.mod.breadmod.datagen.recipe.shapeless

import net.minecraft.data.recipes.RecipeCategory
import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateShapelessRecipeThis(
    val name: String,
    val category: RecipeCategory,

    val count: Int,
    val types: Array<String>,
    val counts: IntArray
)