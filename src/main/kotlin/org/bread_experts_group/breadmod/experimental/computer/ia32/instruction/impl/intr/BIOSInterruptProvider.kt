package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface BIOSInterruptProvider {
	fun handle(processor: IA32Processor)
	fun setError(processor: IA32Processor, code: UByte) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, true)
		processor.a.tl = code
	}

	fun setOK(processor: IA32Processor) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, false)
		processor.a.tl = 0u
	}
}