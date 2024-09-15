package bread.mod.breadmod.datagen.language

import kotlin.annotation.AnnotationTarget.FIELD

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