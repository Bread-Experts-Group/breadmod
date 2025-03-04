package org.bread_experts_group.breadmod.experimental.computer

object BinaryUtil {
	fun hex(l: Long): String = "0x${l.toString(16).padStart(16, '0').uppercase()}"
	fun hex(i: Int): String = "0x${i.toString(16).padStart(8, '0').uppercase()}"
	fun hex(s: Short): String = "0x${s.toString(16).padStart(4, '0').uppercase()}"
	fun hex(b: Byte): String = "0x${b.toString(16).padStart(2, '0').uppercase()}"
	fun hex(l: ULong): String = "0x${l.toString(16).padStart(16, '0').uppercase()}"
	fun hex(i: UInt): String = "0x${i.toString(16).padStart(8, '0').uppercase()}"
	fun hex(s: UShort): String = "0x${s.toString(16).padStart(4, '0').uppercase()}"
	fun hex(b: UByte): String = "0x${b.toString(16).padStart(2, '0').uppercase()}"
	fun absb(b: Byte): Byte = if (b < 0) (-b).toByte() else b
	fun abss(s: Short): Short = if (s < 0) (-s).toShort() else s

	fun iandbDef(a: UInt, b: UByte): UInt = a and (b.toUInt())
	infix fun UInt.and(b: UByte): UInt = this@BinaryUtil.iandbDef(this, b)

	fun sandbDef(a: UShort, b: UByte): UShort = a and (b.toUShort())
	infix fun UShort.and(b: UByte): UShort = this@BinaryUtil.sandbDef(this, b)

	fun shrDef(s: UShort, c: Int): UShort = (s.toUInt() shr c).toUShort()
	fun shlDef(s: UShort, c: Int): UShort = (s.toUInt() shl c).toUShort()
	infix fun UShort.shr(c: Int): UShort = this@BinaryUtil.shrDef(this, c)
	infix fun UShort.shl(c: Int): UShort = this@BinaryUtil.shlDef(this, c)

	fun shrDef(b: UByte, c: Int): UByte = (b.toUInt() shr c).toUByte()
	fun shlDef(b: UByte, c: Int): UByte = (b.toUInt() shl c).toUByte()
	infix fun UByte.shr(c: Int): UByte = this@BinaryUtil.shrDef(this, c)
	infix fun UByte.shl(c: Int): UByte = this@BinaryUtil.shlDef(this, c)

	fun Boolean.toULong(): ULong = if (this) 1u else 0u
	fun Boolean.toUInt(): UInt = if (this) 1u else 0u
	fun Boolean.toUShort(): UShort = if (this) 1u else 0u
	fun Boolean.toUByte(): UByte = if (this) 1u else 0u

	@OptIn(ExperimentalUnsignedTypes::class)
	fun readBinary(length: Int, read: () -> UByte, flip: Boolean = false): Long {
		val buffer = UByteArray(length) { read() }
		if (flip) buffer.reverse()
		var final: Long = 0
		buffer.forEachIndexed { i, b ->
			final = final or (b.toLong() shl (i * 8))
		}
		return final
	}
}