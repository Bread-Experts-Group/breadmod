package org.bread_experts_group.breadmod.datagen.tag

/**
 * An annotation to mark a value for block tag generation.
 *
 * @author Miko Elbrecht
 * @since 1.4.0
 * @see ModTagProvider
 */
@Target(AnnotationTarget.FIELD)
internal annotation class DataGenerateTagBlock(vararg val tags: String)