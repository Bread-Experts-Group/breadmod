package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HC3InstructionRET : Instruction {
	override fun handle16(processor: IA32Processor) {
		val reset = processor.pop16()
		processor.logger.warn("RET16 NEAR $reset")
		processor.ip.tex = reset.toUInt()
	}

	override fun handle32(processor: IA32Processor) {
		val reset = processor.pop32()
		processor.logger.warn("RET32 NEAR $reset")
		processor.ip.tex = reset.toUInt()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}