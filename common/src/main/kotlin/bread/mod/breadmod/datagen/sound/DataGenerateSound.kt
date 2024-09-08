package bread.mod.breadmod.datagen.sound

import kotlin.annotation.AnnotationTarget.FIELD

@Repeatable
@Target(FIELD)
annotation class DataGenerateSound(
    val sound: String,
    val type: Type = Type.FILE,

    val volume: Float = 1f,
    val pitch: Float = 1f,
    val weight: Int = 1,
    val stream: Boolean = false,
    @Suppress("PropertyName") val attenuation_distance: Int = 16,
    val preload: Boolean = false
) {
    enum class Type {
        FILE,
        EVENT
    }
}