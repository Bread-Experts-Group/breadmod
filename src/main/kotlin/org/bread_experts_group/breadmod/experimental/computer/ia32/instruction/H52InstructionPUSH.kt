package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H52InstructionPUSH : Instruction {
	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.d.tx)
	}

	override fun handle32(processor: IA32Processor) {
		processor.push32(processor.d.tex)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}