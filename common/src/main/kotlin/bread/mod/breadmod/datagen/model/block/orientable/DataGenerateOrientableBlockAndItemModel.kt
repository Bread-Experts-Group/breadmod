package bread.mod.breadmod.datagen.model.block.orientable

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateOrientableBlockAndItemModel(
    val side: String = "",
    val front: String = "",
    val top: String = ""
)