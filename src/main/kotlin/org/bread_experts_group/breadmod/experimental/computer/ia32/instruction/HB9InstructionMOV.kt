package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HB9InstructionMOV : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		"cx, ${hex(processor.decoding.readBinaryI(2).toUShort())}"

	override fun handle16(processor: IA32Processor) {
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.c.tx = imm16
	}

	override fun getOperands32(processor: IA32Processor): String =
		"ecx, ${hex(processor.decoding.readBinaryI(4).toUInt())}"

	override fun handle32(processor: IA32Processor) {
		val imm32 = processor.decoding.readBinaryI(4).toUInt()
		processor.c.tex = imm32
	}

	override val supportsCodeSegmentOverride: Boolean = false
}