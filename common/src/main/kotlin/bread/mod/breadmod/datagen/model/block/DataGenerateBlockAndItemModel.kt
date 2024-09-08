package bread.mod.breadmod.datagen.model.block

import kotlin.annotation.AnnotationTarget.FIELD

/**
 * A marker annotation for data generation, specifically that of block and item models.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Target(FIELD)
annotation class DataGenerateBlockAndItemModel