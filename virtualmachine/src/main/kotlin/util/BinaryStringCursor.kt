package breadmod.rnd.util

class BinaryStringCursor(string: String) {
    private val array = string.toCharArray()
    var position: Int = 0
    //var littleEndian: Boolean = true
    fun readByte(): Byte = array[position].code.toByte().also { position += 1; println("0x${it.toString(radix = 16).padStart(2, '0').uppercase()}") }
    fun readShort(): Short = combine(*array.sliceArray(position .. (position + 1))).toShort().also { position += 2; println("0x${it.toString(radix = 16).padStart(2, '0').uppercase()}") }
    fun readInt(): Int = combine(*array.sliceArray(position .. (position + 3))).toInt().also { position += 4; println("0x${it.toString(radix = 16).padStart(2, '0').uppercase()}") }
    fun readLong(): Long = combine(*array.sliceArray(position .. (position + 7))).also { position += 8; println("0x${it.toString(radix = 16).padStart(2, '0').uppercase()}") }
    fun readFloat(): Float = Float.fromBits(readInt())
    fun readDouble(): Double = Double.fromBits(readLong())

    fun skip(bytes: Int) { position += bytes }
    //private fun ByteArray.slice(r: IntRange): ByteArray = this.sliceArray(r).let { if(littleEndian) it else it.reversedArray() }

    companion object {
        fun combine(vararg chars: Char): Long {
            var concat = 0L
            chars.forEach { concat = (concat shl 8) or it.code.toLong() }
            return concat
        }
    }
}