package bread.mod.breadmod.datagen.model.block.simple

import dev.architectury.registry.registries.RegistrySupplier
import kotlin.annotation.AnnotationTarget.FIELD

/**
 * A marker annotation for data generation, specifically that of block and item models.
 *
 * @throws IllegalArgumentException If the property is not of type [RegistrySupplier].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Target(FIELD)
annotation class DataGenerateCubeAllBlockAndItemModel