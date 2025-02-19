package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H8BInstructionMOV : Instruction {
	override fun handle(processor: IA32Processor) {
		processor.fetch()
		if (processor.bitOverride) {
			val modRm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R32)
			modRm.register.set(
				modRm.memRM.decide(
					{ it.get() },
					{
						val physical = (if (processor.csOverride) processor.cs else processor.ds).offset(it)
						processor.computer.requestMemoryAt32(physical).toULong()
					}
				)
			)
		} else {
			val modRm = processor.decoding.getModRM16A(processor.cir, DecodingUtil.AddressingLength.R16)
			modRm.register.set(
				modRm.memRM.decide(
					{ it.get() },
					{
						val physical = (if (processor.csOverride) processor.cs else processor.ds).offset(it)
						processor.computer.requestMemoryAt16(physical).toULong()
					}
				)
			)
		}
	}
}