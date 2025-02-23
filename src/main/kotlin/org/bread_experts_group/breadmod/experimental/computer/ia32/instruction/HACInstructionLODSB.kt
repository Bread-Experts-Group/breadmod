package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object HACInstructionLODSB : Instruction {
	override fun getOperands16(processor: IA32Processor): String = "al, [si [${hex(processor.si.x)}]]"

	override fun handle16(processor: IA32Processor) {
		processor.a.tl = processor.computer.requestMemoryAt(processor.ds.offset(processor.si))
		if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) processor.si.x -= 1u
		else processor.si.x += 1u
	}

	override val supportsCodeSegmentOverride: Boolean = false
}