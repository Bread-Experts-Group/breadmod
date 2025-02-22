package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HEBInstructionJMP : Instruction {
	override fun handle16(processor: IA32Processor) {
		val relative = processor.fetch().let { processor.cir.toByte() }
		processor.logger.warn("JMP SHORT $relative")
		processor.ip.tex = (processor.ip.tex.toInt() + relative).toUInt()
		if (relative.toInt() == -2) throw IllegalStateException("Infinite loop")
	}

	override val supportsCodeSegmentOverride: Boolean = false
}