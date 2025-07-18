package org.bread_experts_group.breadmod.datagen.model.block

/**
 * An annotation to mark a block for simple block / item model generation.
 *
 * @author Miko Elbrecht
 * @since 1.4.0
 * @see ModBlockStateProvider
 */
@Target(AnnotationTarget.FIELD)
internal annotation class DataGenerateModelBlockAndItem(
	val extendedPath: String = "",
	val renderType: String = "solid"
)