package bread.mod.breadmod.datagen.loot

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateLoot(
    val dropType: Type = Type.SELF,
    val optionalBlock: String = "",
    val optionalItem: String = ""
) {
    enum class Type {
        SELF, OTHER, DOOR, SNOW_LAYER
    }
}
