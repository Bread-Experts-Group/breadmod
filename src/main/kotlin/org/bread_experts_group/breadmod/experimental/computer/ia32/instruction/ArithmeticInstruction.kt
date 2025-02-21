package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface ArithmeticInstruction : Instruction {
	fun setFlagsToResult(processor: IA32Processor, result: ULong) {
		processor.setFlag(IA32Processor.FLAGSFlagType.OVERFLOW_FLAG, false) // TODO OVERFLOW
		processor.setFlagToResult(IA32Processor.FLAGSFlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FLAGSFlagType.ZERO_FLAG, result)
		processor.setFlag(IA32Processor.FLAGSFlagType.AUXILIARY_CARRY_FLAG, false) // TODO AUX CARRY
		processor.setFlag(IA32Processor.FLAGSFlagType.CARRY_FLAG, false) // TODO CARRY
		processor.setFlagToResult(IA32Processor.FLAGSFlagType.PARITY_FLAG, result)
	}
}