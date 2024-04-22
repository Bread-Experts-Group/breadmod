package breadmod.rnd.util

/**
 * A growable array, capable of storing up to 4.611686e+18 values.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ExtendedArray<T>(val size: Long? = null) {
    private val arrays = mutableListOf<MutableList<T>>()
    private fun getArrayForOffset(offset: Long): MutableList<T> {
        val arrayPosition = (offset / Int.MAX_VALUE).toInt()
        return arrays.getOrNull(arrayPosition) ?: mutableListOf<T>().also { arrays[arrayPosition] = it }
    }

    operator fun get(offset: Long): T? {
        return if(size != null && offset > size) throw IndexOutOfBoundsException("Out of bounds")
        else getArrayForOffset(offset).getOrNull((offset % Int.MAX_VALUE).toInt())
    }
    operator fun set(offset: Long, value: T) {
        if(size != null && offset > size) throw IndexOutOfBoundsException("Out of bounds")
        else getArrayForOffset(offset)[(offset / Int.MAX_VALUE).toInt()] = value
    }
}