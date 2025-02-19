package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HACInstructionLODSB : Instruction {
	override fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		processor.a.tl = processor.computer.requestMemoryAt(processor.ds.offset(processor.si))
		if (processor.getFlag(IA32Processor.FlagType.DIRECTION_FLAG)) processor.si.x -= 1u
		else processor.si.x += 1u
	}
}