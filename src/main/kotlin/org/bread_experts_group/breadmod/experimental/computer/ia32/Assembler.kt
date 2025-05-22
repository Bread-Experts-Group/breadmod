package org.bread_experts_group.breadmod.experimental.computer.ia32

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.InputStream
import java.io.InputStreamReader

class Assembler(assemblyStream: InputStream) {
	private val logger: Logger = LogManager.getLogger("IA32 Assembler")
	private val assemblyStream: InputStreamReader = InputStreamReader(assemblyStream)
	private val baseDelimiters: CharArray = charArrayOf(' ', '\r', '\n', '\t')
	private fun readToken(vararg delimiters: Char = this.baseDelimiters): String {
		var buffer = ""
		while (true) {
			val nextSz = this.assemblyStream.read()
			if (nextSz == -1) return buffer
			val next = Char(nextSz)
			if (delimiters.contains(next)) {
				if (buffer.isNotEmpty()) return buffer
			} else buffer += next
		}
	}

	enum class Register {
		AL,
		AH,
		AX,
		EAX,
		RAX,
		CL,
		CH,
		CX,
		ECX,
		RCX,
		DL,
		DH,
		DX,
		EDX,
		RDX,
		BL,
		BH,
		BX,
		EBX,
		RBX,
		SP,
		ESP,
		RSP,
		BP,
		EBP,
		RBP,
		SI,
		ESI,
		RSI,
		DI,
		EDI,
		RDI,
		CR0,
		CR2,
		CR3,
		CR4
	}

	fun getRegisterOrModRM() {
		this.logger.warn(this.readToken(','))
		this.logger.warn(this.readToken())
		TODO("meow?")
	}

	enum class BitMode(val bits: Int) {
		BITS_16(16),
		BITS_32(32),
		BITS_64(64);

		companion object {
			val mapping: Map<Int, BitMode> = entries.associateBy { it.bits }
		}
	}

	private var labels: MutableMap<String, ULong> = mutableMapOf()
	private var position: ULong = 0u
	private var mode: BitMode = BitMode.BITS_32

	private fun String.parseULong(): ULong = when (this.substring(0 .. 1)) {
		"0x" -> this.substring(2).toULong(16)
		else -> this.toULong()
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	fun assemble(): UByteArray {
		val assembled = mutableListOf<UByte>()
		while (this.assemblyStream.ready()) {
			when (val token = this.readToken()) {
				"org"  -> this.position = this.readToken().parseULong()
				"bits" -> this.mode = BitMode.mapping.getValue(this.readToken().toInt())
				"xor"  -> {
					this.getRegisterOrModRM()
				}
				else   -> {
					if (token.endsWith(':')) {
						if (token.startsWith('.')) TODO("Sublabel")
						this.labels[token.removeSuffix(":")] = this.position
					} else throw UnsupportedOperationException("Unknown token \"${token}\"")
				}
			}
		}
		return assembled.toUByteArray()
	}
}