package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H31InstructionXOR {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		processor.fetch()
		val result = if (processor.bitOverride) {
			val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32)
			rm.memRM.decide(
				{ (it.get() xor rm.register.get()).also { r -> it.set(r) } },
				{
					(processor.computer.requestMemoryAt32(it) xor rm.register.get().toUInt()).toULong().also { r ->
						processor.computer.setMemoryAt32(it, r.toUInt())
					}
				}
			)
		} else {
			val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R16)
			rm.memRM.decide(
				{ (it.get() xor rm.register.get()).also { r -> it.set(r) } },
				{
					(processor.computer.requestMemoryAt16(it) xor rm.register.get().toUShort()).toULong().also { r ->
						processor.computer.setMemoryAt16(it, r.toUShort())
					}
				}
			)
		}

		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false)
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false)
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
		// AF flag is undefined
	}
}