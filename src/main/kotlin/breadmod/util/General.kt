package breadmod.util

val formatArray = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")
fun formatNumber(number: Double, offset: Int): Pair<Double, String> {
    var num = number
    var index = 3 - offset
    while (num >= 1000 && index < formatArray.size - 1) {
        num /= 1000
        index++
    }
    while (num < 1 && index > 0) {
        num *= 1000
        index--
    }
    return num to formatArray[index]
}

fun formatUnit(from: Double, to: Double, unit: String, formatShort: Boolean, places: Int, offset: Int = 0): String =
    "${String.format("%.${places}f", if(formatShort) formatNumber(from, offset).first else from)} / ${if(formatShort) formatNumber(to, offset).let { "${String.format("%.${places}f", it.first)}${it.second}$unit" } else "$to $unit"} (${String.format("%.${places}f", (from / to)*100)}%)"
fun formatUnit(from: Int, to: Int, unit: String, formatShort: Boolean, places: Int, offset: Int = 0): String =
    formatUnit(from.toDouble(), to.toDouble(), unit, formatShort, places, offset)