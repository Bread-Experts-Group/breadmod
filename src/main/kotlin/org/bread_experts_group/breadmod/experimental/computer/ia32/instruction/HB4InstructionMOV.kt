package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HB4InstructionMOV : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		"ah, ${hex(processor.decoding.readBinaryI(1).toUByte())}"

	override fun handle16(processor: IA32Processor) {
		val imm8 = processor.decoding.readBinaryI(1)
		processor.a.th = imm8.toUByte()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}