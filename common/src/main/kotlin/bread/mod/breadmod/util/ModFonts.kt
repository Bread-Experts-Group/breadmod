package bread.mod.breadmod.util

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.network.chat.Style

/**
 * Universal font styles for Bread Mod.
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
internal object ModFonts {
    /**
     * Filled-in variant of the war timer font.
     * @author Logan McLean (this property), Miko Elbrecht (translation of sprites into font), Tour De Pizza (sprites)
     * @since 1.0.0
     */
    val WARTIMER_INFILL: Style = Style.EMPTY.withFont(modLocation("wartimer_infill"))

// --Commented out by Inspection START (9/10/2024 03:53):
//    /**
//     * Outlined variant of the war timer font.
//     * @author Logan McLean (this property), Miko Elbrecht (translation of sprites into font), Tour De Pizza (sprites)
//     * @since 1.0.0
//     */
//    @Deprecated("Use WARTIMER_INFILL instead", ReplaceWith("WARTIMER_INFILL"))
//    val WARTIMER_OUTLINE: Style = Style.EMPTY.withFont(modLocation("wartimer_outline"))
// --Commented out by Inspection STOP (9/10/2024 03:53)

// --Commented out by Inspection START (9/10/2024 03:53):
//    /**
//     * Public Sans, Regular.
//     * @author Miko Elbrecht (this property), U.S. General Services Administration (font)
//     * @since 1.0.0
//     */
//    @JvmStatic
//    val PUBLIC_SANS_REGULAR: Style = Style.EMPTY.withFont(modLocation("public_sans_regular"))
// --Commented out by Inspection STOP (9/10/2024 03:53)
}