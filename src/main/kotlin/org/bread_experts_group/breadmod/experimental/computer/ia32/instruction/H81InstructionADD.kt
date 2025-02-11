package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H81InstructionADD {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		val (rm, imm) = if (processor.bitOverride) {
			processor.fetch()
			processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32) to
					processor.decoding.readBinaryI(4).toULong()
		} else {
			TODO("16-bit AND 0x81")
		}
		val result = rm.memRM.decide(
			{ (it.get() + imm).also { r -> it.set(r) } },
			{
				(processor.computer.requestMemoryAt32(it) + imm).also { r ->
					processor.computer.setMemoryAt32(it, r.toUInt())
				}
			}
		)

		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false) // TODO OVERFLOW
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlag(IA32Processor.FlagType.AUXILIARY_CARRY_FLAG, false) // TODO AUX CARRY
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false) // TODO CARRY
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
	}
}