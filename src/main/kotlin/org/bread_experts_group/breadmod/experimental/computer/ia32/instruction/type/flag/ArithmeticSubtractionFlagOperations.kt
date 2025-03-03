package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface ArithmeticSubtractionFlagOperations : ArithmeticFlagOperations {
	fun setFlagsForOperationR(processor: IA32Processor, a: UByte, b: UByte): UByte {
		this.auxCarryCheck(processor, a.toULong(), b.toULong())
		processor.flags.setFlag(FlagType.CARRY_FLAG, b > a)
		val subbed = a.toULong() - b
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && subbed.toByte() < 0)) || ((a.toByte() < 0) && (b.toByte() < 0) && subbed.toByte() > 0)
		)
		return subbed.toUByte()
	}

	fun setFlagsForOperationR(processor: IA32Processor, a: UShort, b: UShort): UShort {
		this.auxCarryCheck(processor, a.toULong(), b.toULong())
		processor.flags.setFlag(FlagType.CARRY_FLAG, b > a)
		val subbed = a.toULong() - b
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && subbed.toShort() < 0)) || ((a.toShort() < 0) && (b.toShort() < 0) && subbed.toShort() > 0)
		)
		return subbed.toUShort()
	}

	fun setFlagsForOperationR(processor: IA32Processor, a: UInt, b: UInt): UInt {
		this.auxCarryCheck(processor, a.toULong(), b.toULong())
		processor.flags.setFlag(FlagType.CARRY_FLAG, b > a)
		val subbed = a.toULong() - b
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && subbed.toInt() < 0)) || ((a.toInt() < 0) && (b.toInt() < 0) && subbed.toInt() > 0)
		)
		return subbed.toUInt()
	}

	fun setFlagsForOperationR(processor: IA32Processor, a: ULong, b: ULong): ULong {
		this.auxCarryCheck(processor, a, b)
		processor.flags.setFlag(FlagType.CARRY_FLAG, b > a)
		val subbed = a - b
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && subbed.toLong() < 0)) || ((a.toLong() < 0) && (b.toLong() < 0) && subbed.toLong() > 0)
		)
		return subbed
	}

	fun auxCarryCheck(processor: IA32Processor, a: ULong, b: ULong) {
		processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, ((a xor (a - b) xor b).toUByte() and 0x10u) > 0u)
	}
}