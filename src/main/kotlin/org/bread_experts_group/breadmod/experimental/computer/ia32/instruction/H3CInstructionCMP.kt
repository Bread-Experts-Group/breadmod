package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H3CInstructionCMP : Instruction {
	override fun handle(processor: IA32Processor) {
		if (processor.bitOverride || processor.csOverride) TODO("can't support CS/66")
		processor.fetch()
		val result = (processor.a.tl - processor.cir).toULong()

		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false) // TODO OVERFLOW
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlag(IA32Processor.FlagType.AUXILIARY_CARRY_FLAG, false) // TODO AUX CARRY
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false) // TODO CARRY
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
	}
}