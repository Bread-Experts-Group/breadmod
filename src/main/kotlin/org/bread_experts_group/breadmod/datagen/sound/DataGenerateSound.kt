package org.bread_experts_group.breadmod.datagen.sound

/**
 * An annotation to mark a sound for data generation.
 *
 * @author Miko Elbrecht
 * @since 1.4.0
 * @see ModSoundDefinitionsProvider
 */
@Target(AnnotationTarget.FIELD)
@Repeatable
internal annotation class DataGenerateSound(
	val volume: Double = 1.0,
	val stream: Boolean = false,
	val sound: String = "<null>"
)