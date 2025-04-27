package org.bread_experts_group.breadmod.datagen.model.item

/**
 * An annotation to mark an item for layered item model generation.
 *
 * @author Miko Elbrecht
 * @since 1.4.0
 * @see ModItemModelProvider
 */
@Target(AnnotationTarget.FIELD)
internal annotation class DataGenerateModelLayeredItem(
	vararg val layerTexture: String
)