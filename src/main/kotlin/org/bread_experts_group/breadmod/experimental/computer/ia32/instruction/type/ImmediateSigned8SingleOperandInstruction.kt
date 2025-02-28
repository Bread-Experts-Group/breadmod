package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface ImmediateSigned8SingleOperandInstruction : Immediate8SingleOperandInstruction {
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = this.getOperands(processor, imm8.toByte())
	override fun handle(processor: IA32Processor, imm8: UByte): Unit = this.handle(processor, imm8.toByte())

	fun getOperands(processor: IA32Processor, rel8: Byte): String
	fun handle(processor: IA32Processor, rel8: Byte)
}