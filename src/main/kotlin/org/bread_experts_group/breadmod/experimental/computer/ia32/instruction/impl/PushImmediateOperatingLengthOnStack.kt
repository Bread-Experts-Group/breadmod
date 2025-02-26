package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateOperatingLengthSingleOperandInstruction

@IA32Instruction(0x68u)
object PushImmediateOperatingLengthOnStack : ImmediateOperatingLengthSingleOperandInstruction {
	override fun getMnemonic16(processor: IA32Processor, imm16: UShort): String = "push"
	override fun getOperands16(processor: IA32Processor, imm16: UShort): String = hex(imm16)
	override fun getMnemonic32(processor: IA32Processor, imm32: UInt): String = "push"
	override fun getOperands32(processor: IA32Processor, imm32: UInt): String = hex(imm32)
	override fun handle16(processor: IA32Processor, imm16: UShort) {
		processor.push16(imm16)
	}

	override fun handle32(processor: IA32Processor, imm32: UInt) {
		processor.push32(imm32)
	}
}