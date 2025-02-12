package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HD1InstructionSHR {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		processor.fetch()
		val rmRaw = processor.cir
		val result = if (processor.bitOverride) {
			TODO("32-bit SHR 0xD1")
		} else {
			processor.decoding.getModRM16A(rmRaw, DecodingUtil.AddressingLength.R16).memRM.decide(
				{ (it.get() shr 1).also { r -> it.set(r) } },
				{
					(processor.computer.requestMemoryAt16(it).toULong() shr 1).also { r ->
						processor.computer.setMemoryAt16(it, r.toUShort())
					}
				}
			)
		}

		processor.setFlag(IA32Processor.FlagType.AUXILIARY_CARRY_FLAG, false) // TODO AUX CARRY
		processor.setFlag(IA32Processor.FlagType.OVERFLOW_FLAG, false) // TODO OVERFLOW
		processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false) // TODO CARRY
		processor.setFlagToResult(IA32Processor.FlagType.SIGN_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.ZERO_FLAG, result)
		processor.setFlagToResult(IA32Processor.FlagType.PARITY_FLAG, result)
	}
}