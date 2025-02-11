package org.bread_experts_group.breadmod.experimental.computer

object BinaryUtil {
	fun hex(l: Long): String = "0x${l.toString(16).padStart(16, '0').uppercase()}"
	fun hex(i: Int): String = "0x${i.toString(16).padStart(8, '0').uppercase()}"
	fun hex(l: ULong): String = "0x${l.toString(16).padStart(16, '0').uppercase()}"
	fun hex(i: UInt): String = "0x${i.toString(16).padStart(8, '0').uppercase()}"
	fun hex(s: UShort): String = "0x${s.toString(16).padStart(4, '0').uppercase()}"
	fun hex(b: UByte): String = "0x${b.toString(16).padStart(2, '0').uppercase()}"

	fun readBinary(length: Int, read: () -> Int, flip: Boolean = false): Int {
		val buffer = IntArray(length) { read() }
		if (flip) buffer.reverse()
		var final = 0
		buffer.forEachIndexed { i, b ->
			final = final or (b shl (i * 8))
		}
		return final
	}
}