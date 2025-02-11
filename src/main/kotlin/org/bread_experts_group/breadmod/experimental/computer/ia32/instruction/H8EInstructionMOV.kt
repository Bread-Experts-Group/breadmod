package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H8EInstructionMOV {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		processor.fetch()
		val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R16)
		rm.memRM.decide(
			{ processor.ss.x = it.get() },
			{ processor.ss.t_x = processor.computer.requestMemoryAt16((processor.ds.t_x * 0x10u) + it) }
		)
	}
}