package org.bread_experts_group.breadmod.command.client.bslpack.socket

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.util.Objects

class FailQuickInputStream(private val from: InputStream) : InputStream() {
	override fun read(): Int {
		val next = try {
			from.read()
		} catch (_: IOException) {
			-1
		}
		if (next == -1) {
			this.close()
			throw EOFException()
		}
		return next
	}

	override fun read(b: ByteArray, off: Int, len: Int): Int {
		Objects.checkFromIndexSize(off, len, b.size)
		if (len == 0) return 0
		var i = 0
		try {
			while (i < len) {
				b[off + i] = this.read().toByte()
				i++
			}
		} catch (_: EOFException) {
			if (i == 0) throw EOFException()
		}
		return i
	}

	override fun close() {
		from.close()
		super.close()
	}
}