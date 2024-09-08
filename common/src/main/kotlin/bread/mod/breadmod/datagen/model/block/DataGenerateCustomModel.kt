package bread.mod.breadmod.datagen.model.block

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateCustomModel(val type: ModelType = ModelType.SIMPLE)