package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HFAInstructionCLI {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		// TODO https://www.felixcloutier.com/x86/cli
		processor.setFlag(IA32Processor.FlagType.INTERRUPT_ENABLE_FLAG, false)
	}
}