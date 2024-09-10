package bread.mod.breadmod.datagen.recipe.shaped

import net.minecraft.data.recipes.RecipeCategory
import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateShapedRecipeThis(
    val name: String,
    val category: RecipeCategory,

    val rows: Array<String>,
    val count: Int,
    val definitions: CharArray,
    val resolution: Array<String>
)