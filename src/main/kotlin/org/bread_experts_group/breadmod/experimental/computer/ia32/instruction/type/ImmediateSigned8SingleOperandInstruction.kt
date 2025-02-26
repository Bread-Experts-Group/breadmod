package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface ImmediateSigned8SingleOperandInstruction : Immediate8SingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, imm8: UByte): String = getMnemonic(processor, imm8.toByte())
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = getOperands(processor, imm8.toByte())
	override fun handle(processor: IA32Processor, imm8: UByte) = handle(processor, imm8.toByte())

	fun getMnemonic(processor: IA32Processor, rel8: Byte): String
	fun getOperands(processor: IA32Processor, rel8: Byte): String
	fun handle(processor: IA32Processor, rel8: Byte)
}