package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.BreadMod.Companion.processor
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.readBinary
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

class DecodingUtil(private val processor: IA32Processor) {
	enum class AddressingLength {
		R8,
		R16,
		R32
	}

	enum class RegisterType {
		GENERAL_PURPOSE,
		SEGMENT,
		CONTROL_REGISTER
	}

	fun getRegRM(reg: UInt, type: RegisterType, operandLength: AddressingLength): KMutableProperty0<ULong> {
		val register = when (reg) {
			0b000u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.a
				RegisterType.SEGMENT          -> return this.processor.es::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr0::ex
			}
			0b001u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.c
				RegisterType.SEGMENT          -> return this.processor.cs::x
				RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR1")
			}
			0b010u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.d
				RegisterType.SEGMENT          -> return this.processor.ss::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr2::ex
			}
			0b011u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.b
				RegisterType.SEGMENT          -> return this.processor.ds::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr3::ex
			}
			0b100u -> when (type) {
				RegisterType.GENERAL_PURPOSE  ->
					if (operandLength == AddressingLength.R8) return this.processor.a::h
					else this.processor.sp
				RegisterType.SEGMENT          -> return this.processor.fs::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr4::ex
			}
			0b101u -> when (type) {
				RegisterType.GENERAL_PURPOSE  ->
					if (operandLength == AddressingLength.R8) return this.processor.c::h
					else this.processor.bp
				RegisterType.SEGMENT          -> return this.processor.gs::x
				RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR5")
			}
			0b110u -> when (type) {
				RegisterType.GENERAL_PURPOSE  ->
					if (operandLength == AddressingLength.R8) return this.processor.d::h
					else this.processor.si
				RegisterType.SEGMENT          -> throw IllegalArgumentException("#UD Sreg 110")
				RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR6")
			}
			0b111u -> when (type) {
				RegisterType.GENERAL_PURPOSE  ->
					if (operandLength == AddressingLength.R8) return this.processor.b::h
					else this.processor.di
				RegisterType.SEGMENT          -> throw IllegalArgumentException("#UD Sreg 111")
				RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR7")
			}
			else   -> throw IllegalStateException(hex(reg))
		}
		return when (operandLength) {
			AddressingLength.R8  -> register::l
			AddressingLength.R16 -> register::x
			AddressingLength.R32 -> register::ex
		}
	}

	fun getRegRMDisassembler(reg: UInt, type: RegisterType, operandLength: AddressingLength): String = when (reg) {
		0b000u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (operandLength) {
				AddressingLength.R8  -> "al [${hex(this.processor.a.tl)}]"
				AddressingLength.R16 -> "ax [${hex(this.processor.a.tx)}]"
				AddressingLength.R32 -> "eax [${hex(this.processor.a.tex)}]"
			}
			RegisterType.SEGMENT          -> "es [${hex(this.processor.es.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr0 [${hex(this.processor.cr0.tex)}]"
		}
		0b001u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (operandLength) {
				AddressingLength.R8  -> "cl [${hex(this.processor.c.tl)}]"
				AddressingLength.R16 -> "cx [${hex(this.processor.c.tx)}]"
				AddressingLength.R32 -> "ecx [${hex(this.processor.c.tex)}]"
			}
			RegisterType.SEGMENT          -> "cs [${hex(this.processor.cs.tx)}]"
			RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR1")
		}
		0b010u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (operandLength) {
				AddressingLength.R8  -> "dl [${hex(this.processor.d.tl)}]"
				AddressingLength.R16 -> "dx [${hex(this.processor.d.tx)}]"
				AddressingLength.R32 -> "edx [${hex(this.processor.d.tex)}]"
			}
			RegisterType.SEGMENT          -> "ss [${hex(this.processor.ss.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr2 [${hex(this.processor.cr2.tex)}]"
		}
		0b011u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (operandLength) {
				AddressingLength.R8  -> "bl [${hex(this.processor.b.tl)}]"
				AddressingLength.R16 -> "bx [${hex(this.processor.b.tx)}]"
				AddressingLength.R32 -> "ebx [${hex(this.processor.b.tex)}]"
			}
			RegisterType.SEGMENT          -> "ds [${hex(this.processor.ds.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr3 [${hex(this.processor.cr3.tex)}]"
		}
		0b100u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (operandLength) {
				AddressingLength.R8  -> "ah [${hex(this.processor.a.th)}]"
				AddressingLength.R16 -> "sp [${hex(this.processor.sp.tx)}]"
				AddressingLength.R32 -> "esp [${hex(this.processor.sp.tex)}]"
			}
			RegisterType.SEGMENT          -> "fs [${hex(this.processor.fs.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr4 [${hex(this.processor.cr4.tex)}]"
		}
		0b101u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (operandLength) {
				AddressingLength.R8  -> "ch [${hex(this.processor.c.th)}]"
				AddressingLength.R16 -> "bp [${hex(this.processor.bp.tx)}]"
				AddressingLength.R32 -> "ebp [${hex(this.processor.bp.tex)}]"
			}
			RegisterType.SEGMENT          -> "gs [${hex(this.processor.gs.tx)}]"
			RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR5")
		}
		0b110u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (operandLength) {
				AddressingLength.R8  -> "dh [${hex(this.processor.d.th)}]"
				AddressingLength.R16 -> "si [${hex(this.processor.si.tx)}]"
				AddressingLength.R32 -> "esi [${hex(this.processor.si.tex)}]"
			}
			RegisterType.SEGMENT          -> throw IllegalArgumentException("#UD Sreg 110")
			RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR6")
		}
		0b111u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (operandLength) {
				AddressingLength.R8  -> "bh [${hex(this.processor.b.th)}]"
				AddressingLength.R16 -> "di [${hex(this.processor.di.tx)}]"
				AddressingLength.R32 -> "edi [${hex(this.processor.di.tex)}]"
			}
			RegisterType.SEGMENT          -> throw IllegalArgumentException("#UD Sreg 111")
			RegisterType.CONTROL_REGISTER -> throw IllegalArgumentException("#UD CR7")
		}
		else   -> throw IllegalStateException(hex(reg))
	}

	fun readBinaryFetch(length: Int): Long = readBinary(length, this::readFetch)
	fun readFetch(): UByte = this.processor.fetch().let { this.processor.cir }

	fun decodeSIB(): ULong {
		this.processor.fetch()
		val (scale, index, base) = this.getComponents(this.processor.cir)
		return (when (index) {
			0b000u -> this.processor.a.ex
			0b001u -> this.processor.c.ex
			0b010u -> this.processor.d.ex
			0b011u -> this.processor.b.ex
			0b100u -> this.processor.sp.ex
			0b110u -> this.processor.si.ex
			0b111u -> this.processor.di.ex
			else   -> throw IllegalArgumentException("SIB index $index")
		} * when (scale) {
			0b00u -> 1u
			0b01u -> 2u
			0b10u -> 4u
			0b11u -> 8u
			else  -> throw IllegalArgumentException("SIB scale $scale")
		}) + when (base) {
			0b000u -> this.processor.a.ex
			0b001u -> this.processor.c.ex
			0b010u -> this.processor.d.ex
			0b011u -> this.processor.b.ex
			0b100u -> 0u
			0b101u -> this.processor.bp.ex
			0b110u -> this.processor.si.ex
			0b111u -> this.processor.di.ex
			else   -> throw IllegalArgumentException("SIB base $base")
		}
	}

	fun decodeSIBDisassembler(): String {
		this.processor.fetch()
		val (scale, index, base) = this.getComponents(this.processor.cir)
		return '(' + when (index) {
			0b000u -> "eax"
			0b001u -> "ecx"
			0b010u -> "edx"
			0b011u -> "ebx"
			0b100u -> "esp"
			0b110u -> "esi"
			0b111u -> "edi"
			else   -> throw IllegalArgumentException("SIB index $index")
		} + when (scale) {
			0b00u -> ""
			0b01u -> "*2"
			0b10u -> "*4"
			0b11u -> "*8"
			else  -> throw IllegalArgumentException("SIB scale $scale")
		} + ')' + when (base) {
			0b000u -> "+eax"
			0b001u -> "+ecx"
			0b010u -> "+edx"
			0b011u -> "+ebx"
			0b100u -> ""
			0b101u -> "+ebp"
			0b110u -> "+esi"
			0b111u -> "+edi"
			else   -> throw IllegalArgumentException("SIB base $base")
		}
	}

	fun getMemRM(
		mod: UInt, rm: UInt, regRMType: RegisterType,
		operandLength: AddressingLength
	): MemRM = when (mod) {
		0b00u, 0b01u, 0b10u -> {
			val memRm = when (this.processor.addressSize) {
				AddressingLength.R32 -> when (rm) {
					0b000u -> this.processor.a.ex
					0b001u -> this.processor.c.ex
					0b010u -> this.processor.d.ex
					0b011u -> this.processor.b.ex
					0b100u -> this.decodeSIB()
					0b101u -> this.readBinaryFetch(4).toULong()
					0b110u -> this.processor.si.ex
					0b111u -> this.processor.di.ex
					else   -> throw IllegalArgumentException(hex(rm))
				} + when (mod) {
					0b00u -> 0u
					0b01u -> this.readFetch().toULong()
					0b10u -> this.readBinaryFetch(4).toULong()
					else  -> throw IllegalArgumentException(hex(mod))
				}
				AddressingLength.R16 -> when (rm) {
					0b000u -> this.processor.b.x + this.processor.si.x
					0b001u -> this.processor.b.x + this.processor.di.x
					0b010u -> this.processor.bp.x + this.processor.si.x
					0b011u -> this.processor.bp.x + this.processor.di.x
					0b100u -> this.processor.si.x
					0b101u -> this.processor.di.x
					0b110u -> when (mod) {
						0b00u -> this.readBinaryFetch(2).toULong()
						else  -> this.processor.bp.x
					}
					0b111u -> this.processor.b.x
					else   -> throw IllegalArgumentException(hex(rm))
				} + when (mod) {
					0b00u -> 0u
					0b01u -> this.readFetch().toULong()
					0b10u -> this.readBinaryFetch(2).toULong()
					else  -> throw IllegalArgumentException(hex(mod))
				}
				else                 -> throw UnsupportedOperationException()
			}
			MemRM(null, memRm)
		}
		0b11u               -> MemRM(this.getRegRM(rm, regRMType, operandLength), null)
		else                -> throw IllegalArgumentException("Bad mod: ${hex(mod)}")
	}

	fun getMemRMDisassembler(
		mod: UInt, rm: UInt, regRMType: RegisterType,
		operandLength: AddressingLength
	): String = when (mod) {
		0b00u, 0b01u, 0b10u -> {
			when (this.processor.addressSize) {
				AddressingLength.R32 -> when (rm) {
					0b000u -> "eax [${hex(this.processor.a.tex)}]"
					0b001u -> "ecx [${hex(this.processor.c.tex)}]"
					0b010u -> "edx [${hex(this.processor.d.tex)}]"
					0b011u -> "ebx [${hex(this.processor.b.tex)}]"
					0b100u -> this.decodeSIBDisassembler()
					0b101u -> hex(this.readBinaryFetch(4).toUInt())
					0b110u -> "esi [${hex(this.processor.si.tex)}]"
					0b111u -> "edi [${hex(this.processor.di.tex)}]"
					else   -> throw IllegalArgumentException(hex(rm))
				} + when (mod) {
					0b00u -> ""
					0b01u -> "+${hex(this.readFetch())}"
					0b10u -> "+${hex(this.readBinaryFetch(4).toUInt())}"
					else  -> throw IllegalArgumentException(hex(mod))
				}
				AddressingLength.R16 -> when (rm) {
					0b000u -> "bx+si [${hex(this.processor.b.tx + this.processor.si.tx)}]"
					0b001u -> "bx+di [${hex(this.processor.b.tx + this.processor.di.tx)}]"
					0b010u -> "bp+si [${hex(this.processor.bp.tx + this.processor.si.tx)}]"
					0b011u -> "bp+di [${hex(this.processor.bp.tx + this.processor.di.tx)}]"
					0b100u -> "si [${hex(this.processor.si.tx)}]"
					0b101u -> "di [${hex(this.processor.di.tx)}]"
					0b110u -> when (mod) {
						0b00u -> "+${hex(this.readBinaryFetch(2).toUShort())}"
						else  -> "+bp [${hex(this.processor.bp.tx)}]"
					}
					0b111u -> "+bx [${hex(this.processor.b.x)}]"
					else   -> throw IllegalArgumentException(hex(rm))
				} + when (mod) {
					0b00u -> ""
					0b01u -> "+${hex(this.readFetch())}"
					0b10u -> "+${hex(this.readBinaryFetch(2).toUShort())}"
					else  -> throw IllegalArgumentException(hex(mod))
				}
				else                 -> throw UnsupportedOperationException()
			}
		}
		0b11u               -> this.getRegRMDisassembler(rm, regRMType, operandLength)
		else                -> throw IllegalArgumentException("Bad mod: ${hex(mod)}")
	}

	class MemRM(
		val register: KMutableProperty0<ULong>?,
		val memory: ULong?
	) {
		fun setRMi(value: UInt): Unit =
			if (this.register != null) this.register.set(value.toULong())
			else processor.computer.setMemoryAt32(this.memory!!, value)

		fun setRMs(value: UShort): Unit =
			if (this.register != null) this.register.set(value.toULong())
			else processor.computer.setMemoryAt16(this.memory!!, value)

		fun setRMb(value: UByte): Unit =
			if (this.register != null) this.register.set(value.toULong())
			else processor.computer.setMemoryAt(this.memory!!, value)

		fun getRMMode(): ULong = when (processor.operandSize) {
			AddressingLength.R32 -> this.getRMi().toULong()
			AddressingLength.R16 -> this.getRMs().toULong()
			else                 -> throw UnsupportedOperationException()
		}

		fun getRMi(): UInt =
			if (this.register != null) this.register.get().toUInt()
			else processor.computer.requestMemoryAt32(this.memory!!)

		fun getRMs(): UShort =
			if (this.register != null) this.register.get().toUShort()
			else processor.computer.requestMemoryAt16(this.memory!!)

		fun getRMb(): UByte =
			if (this.register != null) this.register.get().toUByte()
			else processor.computer.requestMemoryAt(this.memory!!)
	}

	data class ModRMResult(
		val memRM: MemRM,
		val register: KMutableProperty0<ULong>
	)

	data class ModRMDisassemblyResult(
		val regMem: String,
		val register: String
	)

	fun getComponents(modRm: UByte): Triple<UInt, UInt, UInt> {
		val mod = modRm.toUInt() shr 6
		val reg = (modRm.toUInt() shr 3) and 0b111u
		val rm = modRm.toUInt() and 0b111u
		return Triple(mod, reg, rm)
	}

	fun getModRM(
		modRm: UByte, registerType: RegisterType,
		operandLength: AddressingLength = this.processor.operandSize
	): ModRMResult {
		val (mod, reg, rm) = this.getComponents(modRm)
		return ModRMResult(
			this.getMemRM(mod, rm, RegisterType.GENERAL_PURPOSE, operandLength),
			this.getRegRM(reg, registerType, operandLength)
		)
	}

	fun getModRMDisassembler(
		modRm: UByte, registerType: RegisterType,
		operandLength: AddressingLength = this.processor.operandSize
	): ModRMDisassemblyResult {
		val (mod, reg, rm) = this.getComponents(modRm)
		return ModRMDisassemblyResult(
			this.getMemRMDisassembler(mod, rm, RegisterType.GENERAL_PURPOSE, operandLength),
			this.getRegRMDisassembler(reg, registerType, operandLength)
		)
	}

	fun getFlagForResult(flag: FlagType, value: ULong): Boolean = when (flag) {
		FlagType.SIGN_FLAG   -> value.toLong() < 0
		FlagType.ZERO_FLAG   -> value == ULong.MIN_VALUE
		FlagType.PARITY_FLAG -> value.toUByte().countOneBits() % 2 == 0
		else -> throw UnsupportedOperationException("Unsupported flag: $flag")
	}

	fun loadDiscIntoMemory(start: ULong, end: ULong, memoryStart: ULong) {
		val disc = this.processor.computer.disc ?: return
		disc.discStream.channel.position(start.toLong())
		this.processor.logger.warn("BIOS CPY ${hex(start)} -> ${hex(end)} @ ${hex(memoryStart)}")
		for (offset in memoryStart .. memoryStart + (end - start)) {
			// TODO Send in chunks
			this.processor.computer.setMemoryAt(offset, disc.discStream.read().toUByte())
		}
	}
}