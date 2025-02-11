package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H89InstructionMOV {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		processor.fetch()
		val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R16)
		rm.memRM.decide(
			{ it.set(rm.register.get()) },
			{ processor.computer.setMemoryAt16(it, rm.register.get().toUShort()) }
		)
	}
}