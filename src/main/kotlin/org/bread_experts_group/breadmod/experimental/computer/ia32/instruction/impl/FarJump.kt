package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateSegmentAndOperatingLengthDoubleOperandInstruction

@IA32Instruction(0xEAu)
object FarJump : ImmediateSegmentAndOperatingLengthDoubleOperandInstruction {
	override fun getMnemonic16(processor: IA32Processor, seg16: UShort, imm16: UShort): String = "ljmp"
	override fun getMnemonic32(processor: IA32Processor, seg16: UShort, imm32: UInt): String = "ljmp"
	override fun getOperands16(processor: IA32Processor, seg16: UShort, imm16: UShort): String =
		"${hex(seg16)}:${hex(imm16).substring(2)}"

	override fun getOperands32(processor: IA32Processor, seg16: UShort, imm32: UInt): String =
		"${hex(seg16)}:${hex(imm32).substring(2)}"

	override fun handle16(processor: IA32Processor, seg16: UShort, imm16: UShort) {
		processor.cs.tx = seg16
		processor.ip.tex = imm16.toUInt()
	}

	override fun handle32(processor: IA32Processor, seg16: UShort, imm32: UInt) {
		processor.cs.tx = seg16
		processor.ip.tex = imm32
	}
}