package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateOperatingLengthSingleOperandInstruction

@IA32Instruction(0xA3u)
object MoveAToSegmentOffset : ImmediateOperatingLengthSingleOperandInstruction {
	override fun getMnemonic16(processor: IA32Processor, imm16: UShort): String = "mov"
	override fun getMnemonic32(processor: IA32Processor, imm32: UInt): String = "mov"
	override fun getOperands16(processor: IA32Processor, imm16: UShort): String =
		"ds:[${hex(imm16)}] [${hex(processor.ds.offset(imm16.toULong()))}], ax"

	override fun getOperands32(processor: IA32Processor, imm32: UInt): String =
		"ds:[${hex(imm32)}] [${hex(processor.ds.offset(imm32.toULong()))}], eax"

	override fun handle16(processor: IA32Processor, imm16: UShort) {
		processor.computer.setMemoryAt16(processor.ds.offset(imm16.toULong()), processor.a.tx)
	}

	override fun handle32(processor: IA32Processor, imm32: UInt) {
		processor.computer.setMemoryAt32(processor.ds.offset(imm32.toULong()), processor.a.tex)
	}
}