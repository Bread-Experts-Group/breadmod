package bread.mod.breadmod.datagen.recipe.special

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateToastingRecipe(
    val name: String,
    val required: String,
    val time: Int = 100,
    val count: Int = 2,
)
