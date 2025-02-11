package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H09InstructionOR {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		val rm = if (processor.bitOverride) {
			processor.fetch()
			processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32)
		} else {
			TODO("16-bit OR 0x09")
		}
		val result = rm.memRM.register.get().get() or rm.register.get()
		rm.register.set(result)

		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false)
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false)
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
	}
}