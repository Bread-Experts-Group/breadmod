package breadmod.rnd.util

import kotlin.math.ceil

/**
 * A growable array, capable of storing up to 4.611686e+18 values.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ExtendedArray<T>(val size: Long, private val default: T) {
    private val arrays: MutableList<MutableList<T>>
    init {
        var adjustedSize = size
        arrays = MutableList(ceil(size.toDouble() / Int.MAX_VALUE).toInt()) {
            val maxLocalSize = (adjustedSize % Int.MAX_VALUE).toInt()
            adjustedSize -= maxLocalSize
            MutableList(maxLocalSize) { default }
        }
    }

    private fun getArrayForOffset(offset: Long): MutableList<T> = arrays[(offset / Int.MAX_VALUE).toInt()]
    operator fun get(offset: Long): T = getArrayForOffset(offset)[(offset % Int.MAX_VALUE).toInt()]
    operator fun set(offset: Long, value: T) = getArrayForOffset(offset).add((offset % Int.MAX_VALUE).toInt(), value)

    fun slice(from: Long, length: Int): List<T> = buildList {
        (from .. (from + length)).forEach { add(this@ExtendedArray[it]) }
    }

    override fun toString(): String = "[${arrays.joinToString()}]"
}