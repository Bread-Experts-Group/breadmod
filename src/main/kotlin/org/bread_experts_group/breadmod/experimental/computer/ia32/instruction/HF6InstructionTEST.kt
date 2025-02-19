package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HF6InstructionTEST : Instruction {
	override fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		processor.fetch()
		val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R8)
		val imm8 = processor.fetch().let { processor.cir }
		val result = rm.memRM.decide(
			{ it.get() and (imm8.toULong()) },
			{ (processor.computer.requestMemoryAt(it) and imm8).toULong() }
		)

		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false)
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false)
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
		// AF flag is undefined
	}
}