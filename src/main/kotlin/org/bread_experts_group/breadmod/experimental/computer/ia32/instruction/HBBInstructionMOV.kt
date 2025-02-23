package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HBBInstructionMOV : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		"bx, ${hex(processor.decoding.readBinaryI(2).toUShort())}"

	override fun handle16(processor: IA32Processor) {
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.b.tx = imm16
	}

	override fun getOperands32(processor: IA32Processor): String =
		"ebx, ${hex(processor.decoding.readBinaryI(4).toUInt())}"

	override fun handle32(processor: IA32Processor) {
		val imm32 = processor.decoding.readBinaryI(4).toUInt()
		processor.b.tex = imm32
	}

	override val supportsCodeSegmentOverride: Boolean = false
}