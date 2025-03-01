package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateOperatingLengthSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations

@IA32Instruction(0x25u)
object ANDImmediateWithA : ImmediateOperatingLengthSingleOperandInstruction, LogicalArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "and"
	override fun getOperands16(processor: IA32Processor, imm16: UShort): String = "ax, ${hex(imm16)}"
	override fun getOperands32(processor: IA32Processor, imm32: UInt): String = "eax, ${hex(imm32)}"
	override fun handle16(processor: IA32Processor, imm16: UShort) {
		val result = processor.a.tx and imm16
		this.setFlagsForResult(processor, result)
		processor.a.tx = result
	}

	override fun handle32(processor: IA32Processor, imm32: UInt) {
		val result = processor.a.tex and imm32
		this.setFlagsForResult(processor, result)
		processor.a.tex = result
	}
}