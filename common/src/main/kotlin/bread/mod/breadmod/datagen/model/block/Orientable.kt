package bread.mod.breadmod.datagen.model.block

import kotlin.annotation.AnnotationTarget.FIELD

/**
 * A marker annotation for data generation, specifically that of block states; orientation.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Target(FIELD)
annotation class Orientable(val type: Type) {
    enum class Type {
        HORIZONTAL,
        ALL_AXIS
    }
}