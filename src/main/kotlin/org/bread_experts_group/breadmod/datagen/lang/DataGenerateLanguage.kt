package org.bread_experts_group.breadmod.datagen.lang

/**
 * An annotation to mark an item for language data generation (for example, en-us, es-mx, ...)
 *
 * @param language The language the annotation item will be translated against.
 * @param name The name of the language item.
 * If the language provider supports automatic name generation, this can be omitted.
 * @param extension The extension to the language tag (for example, musicCategory.item.<extension>)
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see LanguageDataGenerator
 */
@Repeatable
@Target(AnnotationTarget.FIELD)
internal annotation class DataGenerateLanguage(
	val language : String,
	val name : String = "<null>",
	val extension : String = "<null>"
)