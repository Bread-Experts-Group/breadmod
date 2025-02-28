package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateOperatingLengthSingleOperandInstruction

@IA32Instruction(0x2Du)
object SubtractImmediateFromA : ImmediateOperatingLengthSingleOperandInstruction, ArithmeticSubtractionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "sub"
	override fun getOperands16(processor: IA32Processor, imm16: UShort): String = "ax, ${hex(imm16)}"
	override fun getOperands32(processor: IA32Processor, imm32: UInt): String = "eax, ${hex(imm32)}"
	override fun handle16(processor: IA32Processor, imm16: UShort) {
		val result = this.setFlagsForOperationR(processor, processor.a.x, imm16)
		processor.a.x = result
		this.setFlagsForResult(processor, result)
	}

	override fun handle32(processor: IA32Processor, imm32: UInt) {
		val result = this.setFlagsForOperationR(processor, processor.a.x, imm32)
		processor.a.ex = result
		this.setFlagsForResult(processor, result)
	}
}