package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface LogicalArithmeticInstruction : ArithmeticInstruction {
	override fun setFlagsToResult(processor: IA32Processor, result: ULong) {
		processor.setFlag(IA32Processor.FLAGSFlagType.OVERFLOW_FLAG, false)
		processor.setFlag(IA32Processor.FLAGSFlagType.CARRY_FLAG, false)
		processor.setFlagToResult(IA32Processor.FLAGSFlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FLAGSFlagType.ZERO_FLAG, result)
		processor.setFlagToResult(IA32Processor.FLAGSFlagType.PARITY_FLAG, result)
	}
}