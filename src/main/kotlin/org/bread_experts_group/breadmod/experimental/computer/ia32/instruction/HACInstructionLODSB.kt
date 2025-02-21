package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HACInstructionLODSB : Instruction {
	override fun handle16(processor: IA32Processor) {
		processor.a.tl = processor.computer.requestMemoryAt(processor.ds.offset(processor.si))
		if (processor.getFlag(IA32Processor.FLAGSFlagType.DIRECTION_FLAG)) processor.si.x -= 1u
		else processor.si.x += 1u
	}

	override val supportsCodeSegmentOverride: Boolean = false
}