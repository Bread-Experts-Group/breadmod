package bread.mod.breadmod.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import kotlin.system.exitProcess

internal val formatArray: List<String> = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")

/**
 * Limits a number to 1000, and provides a keyword describing it in a shortened format.
 * For example, 1000 → "1", "k".
 *
 * @return A pair containing the limited number and the unit sign.
 * @param pN The number to format.
 * @param pUnitOffset The offset to start at.
 * @param pUnitMax The maximum number to reach before moving to the next unit.
 * @return A pair containing the limited number and the unit.
 * @author Miko Elbrecht
 * @since 1.0
 * @see formatUnit
 * @see formatArray
 */
fun formatNumber(pN: Double, pUnitOffset: Int = 0, pUnitMax: Int = 1000): Pair<Double, String> {
    var num = pN
    var index = 3 + pUnitOffset
    while (num >= pUnitMax && index < formatArray.size - 1) {
        num /= pUnitMax
        index++
    }
    while (num < 1 && index > 0) {
        num *= pUnitMax
        index--
    }
    return num to formatArray[index]
}

/**
 * Formats a number.
 * @return The formatted number: `"X S / Y S W (Z%)"` assuming X is under Y, otherwise `"Y / X S W (Z%)"`.
 * @param pFrom The number to format.
 * @param pTo The maximum number (Y).
 * @param pUnit The label to append at the end (W).
 * @param pFormatShort If the numbers should be shortened with a unit in [formatNumber] (S).
 * @param pDecimals The number of decimals to use when representing [pFrom] / [pTo].
 * @param pUnitOffset The offset to start at in [formatNumber].
 * (Only applicable in [pFormatShort]).
 * @param pUnitMax The maximum number to reach before moving to the next unit in [formatNumber].
 * (Only applicable in [pFormatShort]).
 * @author Miko Elbrecht
 * @see formatNumber
 */
fun formatUnit(
    pFrom: Double,
    pTo: Double,
    pUnit: String,
    pFormatShort: Boolean,
    pDecimals: Int,
    pUnitOffset: Int = 0,
    pUnitMax: Int = 1000
): String {
    val formatStr = "%.${pDecimals}f %s/ %.${pDecimals}f %s (%.${pDecimals}f%%)"
    val percent = (pFrom / pTo) * 100
    if (pFormatShort) {
        val toFormat = formatNumber(pTo, pUnitOffset, pUnitMax)
        val fromFormat = formatNumber(pFrom, pUnitOffset, pUnitMax)
        return String.format(
            formatStr,
            fromFormat.first, if (toFormat.second != fromFormat.second) "${fromFormat.second}$pUnit " else "",
            toFormat.first, toFormat.second + pUnit,
            percent
        )
    } else {
        return String.format(
            formatStr,
            pFrom, "",
            pTo, pUnit,
            percent
        )
    }
}

/**
 * [formatUnit] for integers.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun formatUnit(
    pFrom: Int,
    pTo: Int,
    pUnit: String,
    pFormatShort: Boolean,
    pDecimals: Int,
    pUnitOffset: Int = 0,
    pUnitMax: Int = 1000
): String =
    formatUnit(pFrom.toDouble(), pTo.toDouble(), pUnit, pFormatShort, pDecimals, pUnitOffset, pUnitMax)

/**
 * TODO javadoc
 */
fun <T> Registry<T>.createTagKey(path: String): TagKey<T> =
    TagKey.create(this.key(), ResourceLocation.parse(path))

/// !!! NOTICE !!! ///

// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good Javadoc for them!

/// INTERNAL DEFINITIONS FOLLOW ///

internal fun computerSD(aggressive: Boolean) {
    val runtime = Runtime.getRuntime()
    val os = System.getProperty("os.name")
    when {
        os.contains("win", true) -> {
//            if (aggressive) ACrasherWindows.run()
            runtime.exec(arrayOf("RUNDLL32.EXE", "powrprof.dll,SetSuspendState 0,1,0"))
        }

        os.contains("mac", true) -> {
            runtime.exec(arrayOf("pmset", "sleepnow"))
        }

        os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> {
            if (aggressive) runtime.exec(arrayOf("shutdown", "0"))
            runtime.exec(arrayOf("systemctl", "suspend"))
        }

        else -> if (aggressive) throw IllegalStateException("Screw you! You're no fun.")
    }

    if (aggressive) {
        Thread.sleep(5000)
        exitProcess(0)
    }
}