package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface LogicalArithmeticInstruction : ArithmeticInstruction {
	override fun setFlagsToResult(processor: IA32Processor, result: ULong) {
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, false)
		processor.flags.setFlag(FlagType.CARRY_FLAG, false)
		processor.flags.setFlagToResult(FlagType.SIGN_FLAG, result)
		processor.flags.setFlagToResult(FlagType.ZERO_FLAG, result)
		processor.flags.setFlagToResult(FlagType.PARITY_FLAG, result)
	}
}