package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H59InstructionPOP : Instruction {
	override fun handle16(processor: IA32Processor) {
		processor.c.tx = processor.pop16()
	}

	override fun handle32(processor: IA32Processor) {
		processor.c.tex = processor.pop32()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}