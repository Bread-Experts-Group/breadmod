package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H50InstructionPUSH : Instruction {
	override fun handle32(processor: IA32Processor) {
		processor.push32(processor.a.tex)
	}

	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.a.tx)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}