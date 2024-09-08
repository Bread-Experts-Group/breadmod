package bread.mod.breadmod.datagen.model.block

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateCustomBlockModel(val existingParent: Boolean = false, val type: BlockModelType = BlockModelType.SIMPLE)