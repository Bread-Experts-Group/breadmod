package bread.mod.breadmod.datagen.tag

import kotlin.annotation.AnnotationTarget.FIELD

@Repeatable
@Target(FIELD)
annotation class DataGenerateTag(val registryName: String, val tag: String)