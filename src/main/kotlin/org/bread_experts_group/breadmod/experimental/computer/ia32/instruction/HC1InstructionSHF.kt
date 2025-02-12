package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HC1InstructionSHF {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		processor.fetch()
		val rmRaw = processor.cir
		processor.fetch()
		val shiftCount = processor.cir.toInt()
		if (shiftCount == 0) return
		val result = if (processor.bitOverride) {
			// SHR 32
			processor.decoding.getModRM16A(rmRaw, DecodingUtil.AddressingLength.R32).memRM.decide(
				{ (it.get() shr shiftCount).also { r -> it.set(r) } },
				{
					(processor.computer.requestMemoryAt32(it) shr shiftCount).toULong().also { r ->
						processor.computer.setMemoryAt32(it, r.toUInt())
					}
				}
			)
		} else {
			// SHL 16
			processor.decoding.getModRM16A(rmRaw, DecodingUtil.AddressingLength.R16).memRM.decide(
				{ (it.get() shl shiftCount).also { r -> it.set(r) } },
				{
					(processor.computer.requestMemoryAt16(it).toUInt() shl shiftCount).toUShort().also { r ->
						processor.computer.setMemoryAt16(it, r)
					}.toULong()
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