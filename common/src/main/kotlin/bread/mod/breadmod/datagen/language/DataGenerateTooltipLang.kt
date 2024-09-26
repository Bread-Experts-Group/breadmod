package bread.mod.breadmod.datagen.language

import kotlin.annotation.AnnotationTarget.FIELD

/**
 * Marker annotation for generating tooltip language translations.
 *
 * @param locale The locale of this translation.
 * @param translation The translation of this object for this locale.
 */
@Repeatable
@Target(FIELD)
annotation class DataGenerateTooltipLang(
    val locale: String,
    val translation: String,
//    val type: TooltipType = TooltipType.TOOLTIP
) {
    enum class TooltipType {
        TOOLTIP, JUKEBOX_SONG
    }
}