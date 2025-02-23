package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.readBinary
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister
import java.util.Optional
import kotlin.reflect.KMutableProperty0

class DecodingUtil(private val processor: IA32Processor) {
	enum class AddressingLength {
		R8,
		R16,
		R32
	}

	fun getRegRM(reg: UInt): KMutableProperty0<ULong> {
		val register = when (reg) {
			0b000u -> this.processor.a
			0b001u -> this.processor.c
			0b010u -> this.processor.d
			0b011u -> this.processor.b
			0b100u -> if (processor.operatingModeLocal == AddressingLength.R8) {
				return this.processor.a::h
			} else this.processor.sp
			0b101u -> if (processor.operatingModeLocal == AddressingLength.R8) {
				return this.processor.b::h
			} else this.processor.bp
			0b110u -> if (processor.operatingModeLocal == AddressingLength.R8) {
				return this.processor.c::h
			} else this.processor.si
			0b111u -> if (processor.operatingModeLocal == AddressingLength.R8) {
				return this.processor.d::h
			} else this.processor.di
			else   -> throw IllegalStateException(hex(reg))
		}
		return when (processor.operatingModeLocal) {
			AddressingLength.R8  -> register::l
			AddressingLength.R16 -> register::x
			AddressingLength.R32 -> register::ex
		}
	}

	fun getRegRMDisassembler(reg: UInt): String = when (reg) {
		0b000u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "al"
			AddressingLength.R16 -> "ax"
			AddressingLength.R32 -> "eax"
		}
		0b001u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "cl"
			AddressingLength.R16 -> "cx"
			AddressingLength.R32 -> "ecx"
		}
		0b010u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "dl"
			AddressingLength.R16 -> "dx"
			AddressingLength.R32 -> "edx"
		}
		0b011u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "bl"
			AddressingLength.R16 -> "bx"
			AddressingLength.R32 -> "ebx"
		}
		0b100u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "ah"
			AddressingLength.R16 -> "sp"
			AddressingLength.R32 -> "esp"
		}
		0b101u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "bh"
			AddressingLength.R16 -> "bp"
			AddressingLength.R32 -> "ebp"
		}
		0b110u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "ch"
			AddressingLength.R16 -> "si"
			AddressingLength.R32 -> "esi"
		}
		0b111u -> when (processor.operatingModeLocal) {
			AddressingLength.R8  -> "dh"
			AddressingLength.R16 -> "di"
			AddressingLength.R32 -> "edi"
		}
		else   -> throw IllegalStateException(hex(reg))
	}

	fun getRegRMSregRef(reg: UInt): SegmentRegister = when (reg) {
		0b000u -> this.processor.es
		0b001u -> this.processor.cs
		0b010u -> this.processor.ss
		0b011u -> this.processor.ds
		0b100u -> this.processor.fs
		0b101u -> this.processor.gs
		0b110u -> throw IllegalStateException("Unknown segment register for reg 0b110")
		0b111u -> throw IllegalStateException("Unknown segment register for reg 0b111")
		else   -> throw IllegalStateException(hex(reg))
	}

	fun getRegRMSreg(reg: UInt): KMutableProperty0<ULong> = getRegRMSregRef(reg)::x
	fun getRegRMSregDisassembler(reg: UInt): String = getRegRMSregRef(reg).name

	data class MemRMResult(
		val register: Optional<KMutableProperty0<ULong>>,
		val address: Optional<ULong>
	) {
		fun <T> decide(ifRegister: (KMutableProperty0<ULong>) -> T, ifAddress: (ULong) -> T): T =
			if (this.register.isPresent) ifRegister(this.register.get())
			else ifAddress(this.address.get())
	}

	fun readBinaryI(length: Int, flip: Boolean = false): Long = readBinary(length, {
		this.processor.fetch()
		this.processor.cir
	}, flip)

	fun decodeSIB(): ULong {
		processor.fetch()
		val (mod, reg, disp) = getComponents(processor.cir)
		return when (mod) {
			0b00u -> when (reg) {
				0b100u -> processor.sp.ex
				else   -> TODO("SIB mod 00 reg $reg")
			} + when (disp) {
				0b100u -> 0u
				else   -> TODO("SIB mod 00 disp $disp")
			}
			0b01u -> TODO("SIB mod 01")
			0b10u -> TODO("SIB mod 10")
			0b11u -> when (reg) {
				0b110u -> processor.sp.ex
				else   -> TODO("SIB mod 11 reg $reg")
			} + when (disp) {
				0b101u -> processor.bp.ex * 8u
				else   -> TODO("SIB mod 11 disp $disp")
			}
			else  -> throw IllegalStateException("SIB mod is incorrect: $mod")
		}
	}

	fun decodeSIBDisassembler(): String {
		processor.fetch()
		val (mod, reg, disp) = getComponents(processor.cir)
		return when (mod) {
			0b00u -> when (reg) {
				0b100u -> "esp"
				else   -> TODO("SIB mod 00 reg $reg")
			} + when (disp) {
				0b100u -> ""
				else   -> TODO("SIB mod 00 disp $disp")
			}
			0b01u -> TODO("SIB mod 01 reg $reg")
			0b10u -> TODO("SIB mod 10 reg $reg")
			0b11u -> when (reg) {
				0b110u -> "esi"
				else   -> TODO("SIB mod 11 reg $reg")
			} + when (disp) {
				0b101u -> "+[ebp*8]"
				else   -> TODO("SIB mod 11 disp $disp")
			}
			else  -> throw IllegalStateException("SIB mod is incorrect: $mod")
		}
	}

	fun getMemRM(mod: UInt, rm: UInt): MemRMResult {
		val pair = when (mod) {
			0b00u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u ->
						if (processor.operatingMode == AddressingLength.R32) this.processor.a.ex
						else this.processor.b.x + this.processor.si.x
					0b001u ->
						if (processor.operatingMode == AddressingLength.R32) this.processor.c.ex
						else this.processor.b.x + this.processor.di.x
					0b010u ->
						if (processor.operatingMode == AddressingLength.R32) this.processor.d.ex
						else this.processor.bp.x + this.processor.si.x
					0b011u ->
						if (processor.operatingMode == AddressingLength.R32) this.processor.b.ex
						else this.processor.bp.x + this.processor.di.x
					0b100u ->
						if (processor.operatingMode == AddressingLength.R32) this.decodeSIB()
						else this.processor.si.x
					0b101u ->
						if (processor.operatingMode == AddressingLength.R32) this.readBinaryI(4).toUInt().toULong()
						else this.processor.di.x
					0b110u ->
						if (processor.operatingMode == AddressingLength.R32) this.processor.si.ex
						else this.readBinaryI(2).toUShort().toULong()
					0b111u ->
						if (processor.operatingMode == AddressingLength.R32) this.processor.di.ex
						else this.processor.b.x
					else   -> throw IllegalStateException("Mod 00, RM ${hex(rm)}")
				}
			)
			0b01u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u -> TODO("[BX+SI]+disp8")
					0b001u -> TODO("[BX+DI]+disp8")
					0b010u -> TODO("[BP+SI]+disp8")
					0b011u -> TODO("[BP+DI]+disp8")
					0b100u ->
						if (processor.operatingMode == AddressingLength.R32)
							(this.decodeSIB() + this.readBinaryI(1).toUByte()).toULong()
						else (this.processor.si.tx + this.readBinaryI(1).toUByte()).toULong()
					0b101u -> TODO("[DI]+disp8")
					0b110u -> TODO("[BP]+disp8")
					0b111u -> TODO("[BX]+disp8")
					else   -> throw IllegalStateException("Mod 01, RM ${hex(rm)}")
				}
			)
			0b10u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u -> TODO("[BX+SI]+disp16")
					0b001u -> TODO("[BX+DI]+disp16")
					0b010u ->
						if (processor.operatingMode == AddressingLength.R32)
							(this.processor.d.tex + this.readBinaryI(4).toUInt()).toULong()
						else ((this.processor.bp.tx + this.processor.si.tx) + this.readBinaryI(2).toUShort()).toULong()
					0b011u -> TODO("[BP+DI]+disp16")
					0b100u -> TODO("[SI]+disp16") // [sib+disp32], [si]+disp16
					0b101u -> TODO("[DI]+disp16")
					0b110u -> TODO("[BP]+disp16")
					0b111u ->
						if (processor.operatingMode == AddressingLength.R32)
							(this.processor.di.tex + this.readBinaryI(4).toUInt()).toULong()
						else (this.processor.b.tx + this.readBinaryI(2).toUShort()).toULong()
					else   -> throw IllegalStateException("Mod 10, RM ${hex(rm)}")
				}
			)
			0b11u -> Optional.of(this.getRegRM(rm)) to Optional.empty()
			else  -> throw IllegalStateException(hex(mod))
		}
		return MemRMResult(pair.first, pair.second)
	}

	fun getMemRMDisassembler(
		mod: UInt,
		rm: UInt
	): String = when (mod) {
		0b00u ->
			'[' + when (rm) {
				0b000u ->
					if (processor.operatingMode == AddressingLength.R32) "eax [${hex(this.processor.a.tex)}]"
					else "bx+si [${hex(this.processor.b.tx + this.processor.si.tx)}]"
				0b001u ->
					if (processor.operatingMode == AddressingLength.R32) "ecx [${hex(this.processor.c.tex)}]"
					else "bx+di [${hex(this.processor.b.x + this.processor.di.x)}]"
				0b010u ->
					if (processor.operatingMode == AddressingLength.R32) "edx [${hex(this.processor.d.tex)}]"
					else "bp+si [${hex(this.processor.bp.x + this.processor.si.x)}]"
				0b011u ->
					if (processor.operatingMode == AddressingLength.R32) "ebx [${hex(this.processor.b.tex)}]"
					else "bp+di [${this.processor.bp.x + this.processor.di.x}]"
				0b100u ->
					if (processor.operatingMode == AddressingLength.R32) decodeSIBDisassembler()
					else "si [${hex(this.processor.si.tx)}]"
				0b101u ->
					if (processor.operatingMode == AddressingLength.R32) hex(this.readBinaryI(4).toUInt())
					else "di [${hex(this.processor.di.tx)}]"
				0b110u ->
					if (processor.operatingMode == AddressingLength.R32) "esi [${hex(this.processor.si.tex)}]"
					else hex(this.readBinaryI(2).toUShort())
				0b111u ->
					if (processor.operatingMode == AddressingLength.R32) "edi [${hex(this.processor.di.tex)}]"
					else "bx"
				else   -> throw IllegalStateException("Mod 00, RM ${hex(rm)}")
			} + ']'
		0b01u ->
			'[' + when (rm) {
				0b000u -> TODO("[BX+SI]+disp8")
				0b001u -> TODO("[BX+DI]+disp8")
				0b010u -> TODO("[BP+SI]+disp8")
				0b011u -> TODO("[BP+DI]+disp8")
				0b100u ->
					if (processor.operatingMode == AddressingLength.R32)
						hex(this.decodeSIB() + this.readBinaryI(1).toUByte())
					else this.readBinaryI(1).toUByte().let { "si+${hex(it)} [${hex(this.processor.si.tx + it)}]" }
				0b101u -> TODO("[DI]+disp8")
				0b110u -> TODO("[BP]+disp8")
				0b111u -> TODO("[BX]+disp8")
				else   -> throw IllegalStateException("Mod 01, RM ${hex(rm)}")
			} + ']'
		0b10u ->
			'[' + when (rm) {
				0b000u -> TODO("[BX+SI]+disp16")
				0b001u -> TODO("[BX+DI]+disp16")
				0b010u ->
					if (processor.operatingMode == AddressingLength.R32)
						this.readBinaryI(4).toUInt().let { "edx+${hex(it)} [${hex(processor.d.tex + it)}]" }
					else
						this.readBinaryI(2).toUShort().let {
							"bp+si+${hex(it)} [${hex(processor.bp.tx + processor.si.tx + it)}]"
						}
				0b011u -> TODO("[BP+DI]+disp16")
				0b100u ->
					if (processor.operatingMode == AddressingLength.R32)
						this.readBinaryI(4)
							.toUInt().let {
								"${decodeSIBDisassembler().also { processor.ip.rx-- }}+" +
										"${hex(it)} [${hex(decodeSIB() + it)}]"
							}
					else
						this.readBinaryI(2).toUShort().let {
							"si+${hex(it)} [${hex(processor.si.tx + it)}]"
						}
				0b101u -> TODO("[DI]+disp16")
				0b110u -> TODO("[BP]+disp16")
				0b111u ->
					if (processor.operatingMode == AddressingLength.R32)
						this.readBinaryI(4).toUInt().let { "edi+${hex(it)} [${hex(processor.di.tex + it)}]" }
					else
						this.readBinaryI(2).toUShort().let { "bx+${hex(it)} [${hex(processor.b.tx + it)}]" }
				else   -> throw IllegalStateException("Mod 10, RM ${hex(rm)}")
			} + ']'
		0b11u -> this.getRegRMDisassembler(rm)
		else  -> throw IllegalStateException(hex(mod))
	}

	data class ModRMResult(
		val memRM: MemRMResult,
		val register: KMutableProperty0<ULong>
	)

	fun getComponents(modRm: UByte): Triple<UInt, UInt, UInt> {
		val mod = modRm.toUInt() shr 6
		val reg = (modRm.toUInt() shr 3) and 0b111u
		val rm = modRm.toUInt() and 0b111u
		return Triple(mod, reg, rm)
	}

	fun getModRM(modRm: UByte): Pair<ModRMResult, UInt> {
		val (mod, reg, rm) = getComponents(modRm)
		return ModRMResult(
			this.getMemRM(mod, rm),
			this.getRegRM(reg)
		) to reg
	}

	fun getModRMDisassembler(modRm: UByte): Pair<Pair<String, String>, UInt> {
		val (mod, reg, rm) = getComponents(modRm)
		return (this.getMemRMDisassembler(mod, rm) to this.getRegRMDisassembler(reg)) to reg
	}

	fun getModRMSreg(modRm: UByte): ModRMResult {
		val (mod, reg, rm) = getComponents(modRm)
		return ModRMResult(this.getMemRM(mod, rm), this.getRegRMSreg(reg))
	}

	fun getModRMSregDisassembler(modRm: UByte): Pair<String, String> {
		val (mod, reg, rm) = getComponents(modRm)
		return this.getMemRMDisassembler(mod, rm) to this.getRegRMSregDisassembler(reg)
	}

	fun getFlagForResult(flag: FlagType, value: ULong): Boolean = when (flag) {
		FlagType.SIGN_FLAG   -> value.toLong() < 0
		FlagType.ZERO_FLAG   -> value == ULong.MIN_VALUE
		FlagType.PARITY_FLAG -> value.countOneBits() % 2 == 0
		else                 -> TODO("Unsupported flag: $flag")
	}
}