package bread.mod.breadmod.datagen

import bread.mod.breadmod.datagen.model.block.ModelType
import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateCustomModel(val existingParent: Boolean = false, val type: ModelType = ModelType.SIMPLE)