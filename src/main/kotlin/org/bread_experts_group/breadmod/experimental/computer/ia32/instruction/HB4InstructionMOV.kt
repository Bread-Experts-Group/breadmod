package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HB4InstructionMOV : Instruction {
	override fun handle16(processor: IA32Processor) {
		val imm8 = processor.fetch().let { processor.cir.toInt() }
		processor.a.th = imm8.toUByte()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}