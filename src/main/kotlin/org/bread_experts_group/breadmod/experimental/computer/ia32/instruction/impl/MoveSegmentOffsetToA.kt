package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateOperatingLengthSingleOperandInstruction

@IA32Instruction(0xA1u)
object MoveSegmentOffsetToA : ImmediateOperatingLengthSingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "mov"
	override fun getOperands16(processor: IA32Processor, imm16: UShort): String =
		"ax, ds:[${hex(imm16)}] [${hex(processor.ds.offset(imm16.toULong()))}]"

	override fun getOperands32(processor: IA32Processor, imm32: UInt): String =
		"eax, ds:[${hex(imm32)}] [${hex(processor.ds.offset(imm32.toULong()))}]"

	override fun handle16(processor: IA32Processor, imm16: UShort) {
		processor.a.tx = processor.computer.requestMemoryAt16(processor.ds.offset(imm16.toULong()))
	}

	override fun handle32(processor: IA32Processor, imm32: UInt) {
		processor.a.tex = processor.computer.requestMemoryAt32(processor.ds.offset(imm32.toULong()))
	}
}