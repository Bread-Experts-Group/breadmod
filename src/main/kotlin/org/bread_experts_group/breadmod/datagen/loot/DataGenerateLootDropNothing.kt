package org.bread_experts_group.breadmod.datagen.loot

/**
 * An annotation to mark a block as dropping nothing for loot data generation purposes.
 *
 * @author Miko Elbrecht
 * @since 1.4.0
 * @see ModBlockLootProvider
 */
@Target(AnnotationTarget.FIELD)
internal annotation class DataGenerateLootDropNothing(vararg val tags: String)