package bread.mod.breadmod.datagen.language

import kotlin.annotation.AnnotationTarget.FIELD

/**
 * A marker annotation for data generation, specifically that of language translations for a specific locale.
 *
 * @param locale The locale of this translation.
 * @param translation The translation of this object for this locale.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Repeatable
@Target(FIELD)
annotation class DataGenerateLanguage(val locale: String, val translation: String)