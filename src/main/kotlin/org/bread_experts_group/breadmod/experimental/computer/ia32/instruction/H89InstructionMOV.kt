package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H89InstructionMOV {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		processor.fetch()
		if (processor.bitOverride) {
			val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32)
			rm.memRM.decide(
				{ it.set(rm.register.get()) },
				{ processor.computer.setMemoryAt32(it, rm.register.get().toUInt()) }
			)
		} else {
			val rm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R16)
			rm.memRM.decide(
				{ it.set(rm.register.get()) },
				{ processor.computer.setMemoryAt16(it, rm.register.get().toUShort()) }
			)
		}
	}
}