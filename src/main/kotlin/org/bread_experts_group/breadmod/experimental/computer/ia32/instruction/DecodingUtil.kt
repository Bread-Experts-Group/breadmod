package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.readBinary
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import java.util.Optional
import kotlin.reflect.KMutableProperty0

class DecodingUtil(private val processor: IA32Processor) {
	enum class AddressingLength {
		R8,
		R16,
		R32
	}

	fun getRegRM(
		reg: UInt,
		length: AddressingLength
	): KMutableProperty0<ULong> {
		val register = when (reg) {
			0b000u -> this.processor.a
			0b001u -> this.processor.c
			0b010u -> this.processor.d
			0b011u -> this.processor.b
			0b100u -> if (length == AddressingLength.R8) {
				return this.processor.a::h
			} else this.processor.sp
			0b101u -> if (length == AddressingLength.R8) {
				return this.processor.b::h
			} else this.processor.bp
			0b110u -> if (length == AddressingLength.R8) {
				return this.processor.c::h
			} else this.processor.si
			0b111u -> if (length == AddressingLength.R8) {
				return this.processor.d::h
			} else this.processor.di
			else   -> throw IllegalStateException(hex(reg))
		}
		return when (length) {
			AddressingLength.R8  -> register::l
			AddressingLength.R16 -> register::x
			AddressingLength.R32 -> register::ex
		}
	}

	fun getRegRMSreg(reg: UInt): KMutableProperty0<ULong> = when (reg) {
		0b000u -> this.processor.es
		0b001u -> this.processor.cs
		0b010u -> this.processor.ss
		0b011u -> this.processor.ds
		0b100u -> this.processor.fs
		0b101u -> this.processor.gs
		0b110u -> throw IllegalStateException("Unknown segment register for reg 0b110")
		0b111u -> throw IllegalStateException("Unknown segment register for reg 0b111")
		else   -> throw IllegalStateException(hex(reg))
	}::x

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
		val mod = processor.cir.toUInt() shr 6
		val reg = (processor.cir.toUInt() shr 3) and 0b111u
		val disp = processor.cir.toUInt() and 0b111u
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
			0b11u -> TODO("SIB mod 11")
			else  -> throw IllegalStateException("SIB mod is incorrect: $mod")
		}
	}

	fun getMemRM(
		mod: UInt,
		rm: UInt,
		length: AddressingLength
	): MemRMResult {
		val pair = when (mod) {
			0b00u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u ->
						if (length == AddressingLength.R32) this.processor.a.ex
						else this.processor.b.x + this.processor.si.x
					0b001u ->
						if (length == AddressingLength.R32) this.processor.c.ex
						else this.processor.b.x + this.processor.di.x
					0b010u ->
						if (length == AddressingLength.R32) this.processor.d.ex
						else this.processor.bp.x + this.processor.si.x
					0b011u ->
						if (length == AddressingLength.R32) this.processor.b.ex
						else this.processor.bp.x + this.processor.di.x
					0b100u ->
						if (length == AddressingLength.R32) this.decodeSIB()
						else this.processor.si.x
					0b101u ->
						if (length == AddressingLength.R32) this.readBinaryI(4).toUInt().toULong()
						else this.processor.di.x
					0b110u ->
						if (length == AddressingLength.R32) this.processor.si.ex
						else this.readBinaryI(2).toUShort().toULong()
					0b111u ->
						if (length == AddressingLength.R32) this.processor.di.ex
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
						if (length == AddressingLength.R32) (this.decodeSIB() + this.readBinaryI(1).toUByte()).toULong()
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
						if (length == AddressingLength.R32)
							(this.processor.d.tex + this.readBinaryI(4).toUInt()).toULong()
						else ((this.processor.bp.tx + this.processor.si.tx) + this.readBinaryI(2).toUShort()).toULong()
					0b011u -> TODO("[BP+DI]+disp16")
					0b100u -> TODO("[SI]+disp16")
					0b101u -> TODO("[DI]+disp16")
					0b110u -> TODO("[BP]+disp16")
					0b111u ->
						if (length == AddressingLength.R32)
							(this.processor.di.tex + this.readBinaryI(4).toUInt()).toULong()
						else (this.processor.b.tx + this.readBinaryI(2).toUShort()).toULong()
					else   -> throw IllegalStateException("Mod 10, RM ${hex(rm)}")
				}
			)
			0b11u -> Optional.of(this.getRegRM(rm, length)) to Optional.empty()
			else  -> throw IllegalStateException(hex(mod))
		}
		return MemRMResult(pair.first, pair.second)
	}

	data class ModRMResult(
		val memRM: MemRMResult,
		val register: KMutableProperty0<ULong>
	)

	fun getModRM(
		modRm: UByte,
		length: AddressingLength = processor.operatingMode(true)
	): Pair<ModRMResult, UInt> {
		val mod = modRm.toUInt() shr 6
		val reg = (modRm.toUInt() shr 3) and 0b111u
		val rm = modRm.toUInt() and 0b111u
		return ModRMResult(this.getMemRM(mod, rm, length), this.getRegRM(reg, length)) to reg
	}

	fun getModRMSreg(
		modRm: UByte,
		length: AddressingLength = processor.operatingMode(true)
	): ModRMResult {
		val mod = modRm.toUInt() shr 6
		val reg = (modRm.toUInt() shr 3) and 0b111u
		val rm = modRm.toUInt() and 0b111u
		return ModRMResult(this.getMemRM(mod, rm, length), this.getRegRMSreg(reg))
	}

	fun getFlagForResult(flag: IA32Processor.FLAGSFlagType, value: ULong): Boolean = when (flag) {
		IA32Processor.FLAGSFlagType.SIGN_FLAG   -> value.toLong() < 0
		IA32Processor.FLAGSFlagType.ZERO_FLAG   -> value == ULong.MIN_VALUE
		IA32Processor.FLAGSFlagType.PARITY_FLAG -> value.countOneBits() % 2 == 0
		else                                    -> TODO("Unsupported flag: $flag")
	}
}