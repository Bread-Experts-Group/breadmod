package bread.mod.breadmod.datagen.loot

import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
annotation class DataGenerateLoot(val dropType: Type = Type.SELF) {
    enum class Type {
        SELF, OTHER
    }
}
