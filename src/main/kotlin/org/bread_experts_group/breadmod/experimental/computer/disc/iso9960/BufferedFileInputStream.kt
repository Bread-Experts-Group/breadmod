package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.readBinary
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SeekableByteChannel
import java.util.Optional

class BufferedFileInputStream(val channel: SeekableByteChannel) : InputStream() {
	override fun toString(): String {
		return "${this::class.simpleName}[${this.channel.size()}]"
	}

	override fun read(): Int {
		val byteBuf: ByteBuffer = ByteBuffer.allocateDirect(1)
		this.channel.read(byteBuf)
		byteBuf.flip()
		return byteBuf.get().toUByte().toInt()
	}

	fun readUB(): UByte = this.read().toUByte()

	override fun readNBytes(len: Int): ByteArray {
		if (len == 0) return byteArrayOf()
		val newByteBuf: ByteBuffer = ByteBuffer.allocate(len)
		this.channel.read(newByteBuf)
		return newByteBuf.array()
	}

	fun readBinaryS(length: Int, flip: Boolean = false): Long = readBinary(length, this::readUB, flip)

	fun readLSB(length: Int): Optional<Long> = this.readBinaryS(length).let {
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) Optional.empty()
		else Optional.of(it)
	}

	fun readMSB(length: Int): Optional<Long> = this.readBinaryS(length).let {
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) Optional.empty()
		else Optional.of(it)
	}

	fun readLSBMSB(length: Int): Long = if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		this.skip(length.toLong())
		this.readBinaryS(length)
	} else this.readBinaryS(length).also {
		this.skip(length.toLong())
	}
}