package breadmod.util

import breadmod.ModMain.modLocation
import net.minecraft.network.chat.Style

object ModFonts {
    /** Filled in version of the war timer font */
    val WARTIMER_INFILL: Style = Style.EMPTY.withFont(modLocation("wartimer_infill"))
    /** Outlined version of the war timer font */
    val WARTIMER_OUTLINE: Style = Style.EMPTY.withFont(modLocation("wartimer_outline"))
}