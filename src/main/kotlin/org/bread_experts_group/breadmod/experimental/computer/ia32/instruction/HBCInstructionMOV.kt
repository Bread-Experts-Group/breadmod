package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HBCInstructionMOV : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		"sp, ${hex(processor.decoding.readBinaryI(2).toUShort())}"

	override fun handle16(processor: IA32Processor) {
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.sp.tx = imm16
	}

	override val supportsCodeSegmentOverride: Boolean = false
}