package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H56InstructionPUSH : Instruction {
	override fun handle32(processor: IA32Processor) {
		processor.push32(processor.si.tex)
	}

	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.si.tx)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}