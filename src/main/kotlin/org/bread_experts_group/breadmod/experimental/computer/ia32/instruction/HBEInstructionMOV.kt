package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HBEInstructionMOV : Instruction {
	override fun handle16(processor: IA32Processor) {
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.si.tx = imm16
	}

	override val supportsCodeSegmentOverride: Boolean = false
}