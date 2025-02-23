package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface ArithmeticInstruction : Instruction {
	fun setFlagsToResult(processor: IA32Processor, result: ULong) {
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, false) // TODO OVERFLOW
		processor.flags.setFlagToResult(FlagType.SIGN_FLAG, result)
		processor.flags.setFlagToResult(FlagType.ZERO_FLAG, result)
		processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, false) // TODO AUX CARRY
		processor.flags.setFlag(FlagType.CARRY_FLAG, false) // TODO CARRY
		processor.flags.setFlagToResult(FlagType.PARITY_FLAG, result)
	}
}