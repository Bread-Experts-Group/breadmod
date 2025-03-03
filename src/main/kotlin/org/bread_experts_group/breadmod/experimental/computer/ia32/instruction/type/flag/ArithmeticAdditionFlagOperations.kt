package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface ArithmeticAdditionFlagOperations : ArithmeticFlagOperations {
	fun setFlagsForOperationR(processor: IA32Processor, a: UByte, b: UByte): UByte {
		this.auxCarryCheck(processor, a.toULong(), b.toULong())
		val added = a.toUShort() + b
		processor.flags.setFlag(FlagType.CARRY_FLAG, added > UByte.MAX_VALUE)
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && added.toByte() < 0)) || ((a.toByte() < 0) && (b.toByte() < 0) && added.toByte() > 0)
		)
		return added.toUByte()
	}

	fun setFlagsForOperationR(processor: IA32Processor, a: UShort, b: UShort): UShort {
		this.auxCarryCheck(processor, a.toULong(), b.toULong())
		val added = a.toUInt() + b
		processor.flags.setFlag(FlagType.CARRY_FLAG, added > UShort.MAX_VALUE)
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && added.toShort() < 0)) || ((a.toShort() < 0) && (b.toShort() < 0) && added.toShort() > 0)
		)
		return added.toUShort()
	}

	fun setFlagsForOperationR(processor: IA32Processor, a: UInt, b: UInt): UInt {
		this.auxCarryCheck(processor, a.toULong(), b.toULong())
		val added = a.toULong() + b
		processor.flags.setFlag(FlagType.CARRY_FLAG, added > UInt.MAX_VALUE)
		processor.flags.setFlag(
			FlagType.CARRY_FLAG,
			((a > 0u && b > 0u && added.toInt() < 0)) || ((a.toInt() < 0) && (b.toInt() < 0) && added.toInt() > 0)
		)
		return added.toUInt()
	}

	fun auxCarryCheck(processor: IA32Processor, a: ULong, b: ULong) {
		processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, (((a and 0xFu) + b) shr 4).toUInt() > 0u)
	}
}