package bread.mod.breadmod.datagen.sound

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateSound(
    val name: String,
    val volume: Float,
    val pitch: Float = 1f,
    val stream: Boolean = false
)
