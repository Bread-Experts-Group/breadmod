package org.bread_experts_group.breadmod.command.client.bslpack

import java.io.EOFException
import java.io.IOException
import java.io.OutputStream

class FailQuickOutputStream(private val to: OutputStream) : OutputStream() {
	override fun write(b: Int) {
		try {
			to.write(b)
		} catch (e: IOException) {
			this.close()
			throw EOFException(e.message)
		}
	}

	override fun close() {
		to.close()
		super.close()
	}
}