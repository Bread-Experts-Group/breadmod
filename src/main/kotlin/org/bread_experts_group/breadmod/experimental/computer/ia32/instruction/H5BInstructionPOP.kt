package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H5BInstructionPOP : Instruction {
	override fun handle16(processor: IA32Processor) {
		processor.b.tx = processor.pop16()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}