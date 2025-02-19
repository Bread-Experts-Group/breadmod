package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H39InstructionCMP : Instruction {
	override fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		processor.fetch()
		val result = if (processor.bitOverride) {
			val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32)
			rm.register.get() - rm.memRM.decide(
				{ it.get() },
				{ processor.computer.requestMemoryAt32(it).toULong() }
			)
		} else {
			val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R16)
			rm.register.get() - rm.memRM.decide(
				{ it.get() },
				{ processor.computer.requestMemoryAt16(it).toULong() }
			)
		}

		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false) // TODO OVERFLOW
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlag(IA32Processor.FlagType.AUXILIARY_CARRY_FLAG, false) // TODO AUX CARRY
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false) // TODO CARRY
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
	}
}