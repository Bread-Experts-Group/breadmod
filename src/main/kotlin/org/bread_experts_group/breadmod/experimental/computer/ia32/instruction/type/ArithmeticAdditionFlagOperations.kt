package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface ArithmeticAdditionFlagOperations : ArithmeticFlagOperations {
	fun setFlagsForOperation(processor: IA32Processor, a: ULong, b: UShort) {
		this.auxCarryCheck(processor, a, b.toULong())
		val added = a + b
		processor.flags.setFlag(FlagType.CARRY_FLAG, added > UShort.MAX_VALUE)
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && added.toShort() < 0)) || ((a.toShort() < 0) && (b.toShort() < 0) && added.toShort() > 0)
		)
	}

	fun setFlagsForOperation(processor: IA32Processor, a: ULong, b: UInt) {
		this.auxCarryCheck(processor, a, b.toULong())
		val added = a + b
		processor.flags.setFlag(FlagType.CARRY_FLAG, added > UInt.MAX_VALUE)
		processor.flags.setFlag(
			FlagType.CARRY_FLAG,
			((a > 0u && b > 0u && added.toInt() < 0)) || ((a.toInt() < 0) && (b.toInt() < 0) && added.toInt() > 0)
		)
	}

	fun auxCarryCheck(processor: IA32Processor, a: ULong, b: ULong) {
		processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, (((a and 0xFu) + b) shr 4).toUInt() > 0u)
	}
}