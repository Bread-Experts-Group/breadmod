package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface ArithmeticSubtractionFlagOperations : ArithmeticFlagOperations {
	fun setFlagsForOperation(processor: IA32Processor, a: ULong, b: ULong) {
		this.auxCarryCheck(processor, a, b)
		processor.flags.setFlag(FlagType.CARRY_FLAG, b > a)
		val subbed = a - b
		processor.flags.setFlag(
			FlagType.OVERFLOW_FLAG,
			((a > 0u && b > 0u && subbed.toInt() < 0)) || ((a.toInt() < 0) && (b.toInt() < 0) && subbed.toInt() > 0)
		)
	}

	fun auxCarryCheck(processor: IA32Processor, a: ULong, b: ULong) {
		processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, ((a xor (a - b) xor b) and 0x10u) > 0u)
	}
}