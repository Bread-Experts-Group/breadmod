package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H8BInstructionMOV {
	fun handle(processor: IA32Processor) {
		if (processor.bitOverride) {
			processor.fetch()
			val modRm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32)
			modRm.register.set(
				modRm.memRM.decide(
					{ it.get() },
					{
						val physical = ((if (processor.csOverride) processor.cs else processor.ds).ex) * 0x10u
						processor.computer.requestMemoryAt32(physical + it).toULong()
					}
				)
			)
			return
		}
		TODO("16-bit MOV 0x8B")
	}
}