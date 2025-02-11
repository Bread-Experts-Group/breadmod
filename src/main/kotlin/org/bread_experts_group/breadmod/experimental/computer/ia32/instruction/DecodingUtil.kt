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

	data class MemRMResult(
		val register: Optional<KMutableProperty0<ULong>>,
		val address: Optional<ULong>
	) {
		fun <T> decide(ifRegister: (KMutableProperty0<ULong>) -> T, ifAddress: (ULong) -> T): T =
			if (this.register.isPresent) ifRegister(this.register.get())
			else ifAddress(this.address.get())
	}

	fun readBinaryI(length: Int, flip: Boolean = false): Int = readBinary(length, {
		this.processor.fetch()
		this.processor.cir.toInt()
	}, flip)

	fun getMemRM(
		mod: UInt,
		rm: UInt,
		length: AddressingLength
	): MemRMResult {
		val pair = when (mod) {
			0b00u -> Optional.empty<KMutableProperty0<ULong>>() to when (rm) {
				0b000u -> Optional.of(this.processor.b.x + this.processor.si.x)
				0b001u -> Optional.of(this.processor.b.x + this.processor.di.x)
				0b010u -> Optional.of(this.processor.bp.x + this.processor.si.x)
				0b011u -> Optional.of(this.processor.bp.x + this.processor.di.x)
				0b100u -> Optional.of(this.processor.si.x)
				0b101u -> Optional.of(this.processor.di.x)
				0b110u -> TODO("disp16")
				0b111u -> Optional.of(this.processor.b.x)
				else   -> throw IllegalStateException("Mod 00, RM ${hex(rm)}")
			}
			0b01u -> Optional.empty<KMutableProperty0<ULong>>() to when (rm) {
				0b000u -> TODO("[BX+SI]+disp8")
				0b001u -> TODO("[BX+DI]+disp8")
				0b010u -> TODO("[BP+SI]+disp8")
				0b011u -> TODO("[BP+DI]+disp8")
				0b100u -> TODO("[SI]+disp8")
				0b101u -> TODO("[DI]+disp8")
				0b110u -> TODO("[BP]+disp8")
				0b111u -> TODO("[BX]+disp8")
				else -> throw IllegalStateException("Mod 01, RM ${hex(rm)}")
			}
			0b10u -> Optional.empty<KMutableProperty0<ULong>>() to when (rm) {
				0b000u -> TODO("[BX+SI]+disp16")
				0b001u -> TODO("[BX+DI]+disp16")
				0b010u -> TODO("[BP+SI]+disp16")
				0b011u -> TODO("[BP+DI]+disp16")
				0b100u -> TODO("[SI]+disp16")
				0b101u -> TODO("[DI]+disp16")
				0b110u -> TODO("[BP]+disp16")
				0b111u -> Optional.of((this.processor.b.t_x + this.readBinaryI(2).toUShort()).toULong())
				else -> throw IllegalStateException("Mod 10, RM ${hex(rm)}")
			}
			0b11u -> Optional.of(this.getRegRM(rm, length)) to Optional.empty()
			else  -> throw IllegalStateException(hex(mod))
		}
		return MemRMResult(pair.first, pair.second)
	}

	data class ModRMResult(
		val memRM: MemRMResult,
		val register: KMutableProperty0<ULong>
	)

	fun getModRM16A(
		modRm: UByte,
		length: AddressingLength
	): ModRMResult {
		val mod = modRm.toUInt() shr 6
		val reg = (modRm.toUInt() shr 3) and 0b111u
		val rm = modRm.toUInt() and 0b111u
		return ModRMResult(this.getMemRM(mod, rm, length), this.getRegRM(reg, length))
	}

	fun getFlagForResult(flag: IA32Processor.FlagType, value: ULong): Boolean = when (flag) {
		IA32Processor.FlagType.SIGN_FLAG   -> value < 0u
		IA32Processor.FlagType.ZERO_FLAG   -> value == ULong.MIN_VALUE
		IA32Processor.FlagType.PARITY_FLAG -> value.countOneBits() % 2 == 0
		else                               -> TODO("Unsupported flag: $flag")
	}
}