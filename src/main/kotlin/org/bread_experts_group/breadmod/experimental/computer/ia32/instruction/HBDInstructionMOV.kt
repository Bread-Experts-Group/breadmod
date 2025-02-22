package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HBDInstructionMOV : Instruction {
	override fun handle16(processor: IA32Processor) {
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.bp.tx = imm16
	}

	override fun handle32(processor: IA32Processor) {
		val imm32 = processor.decoding.readBinaryI(4).toUInt()
		processor.bp.tex = imm32
	}

	override val supportsCodeSegmentOverride: Boolean = false
}