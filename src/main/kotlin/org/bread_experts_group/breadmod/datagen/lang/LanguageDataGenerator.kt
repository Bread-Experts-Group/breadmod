package org.bread_experts_group.breadmod.datagen.lang

/**
 * An annotation to mark a class as usable for language data generation (for example, en-us, es-mx, ...)
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see DataGenerateLanguage
 */
@Target(AnnotationTarget.CLASS)
internal annotation class LanguageDataGenerator