package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HB8InstructionMOV : Instruction {
	override fun handle16(processor: IA32Processor) {
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.a.tx = imm16
	}

	override fun handle32(processor: IA32Processor) {
		val imm32 = processor.decoding.readBinaryI(4).toUInt()
		processor.a.tex = imm32
	}

	override val supportsCodeSegmentOverride: Boolean = false
}