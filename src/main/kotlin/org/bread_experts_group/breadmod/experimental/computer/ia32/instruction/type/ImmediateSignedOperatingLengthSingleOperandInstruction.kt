package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface ImmediateSignedOperatingLengthSingleOperandInstruction : ImmediateOperatingLengthSingleOperandInstruction {
	fun getMnemonic32(processor: IA32Processor, rel32: Int): String
	fun getOperands32(processor: IA32Processor, rel32: Int): String
	fun handle32(processor: IA32Processor, rel32: Int)
	fun getMnemonic16(processor: IA32Processor, rel16: Short): String
	fun getOperands16(processor: IA32Processor, rel16: Short): String
	fun handle16(processor: IA32Processor, rel16: Short)

	override fun getMnemonic32(processor: IA32Processor, imm32: UInt): String =
		getMnemonic32(processor, imm32.toInt())

	override fun getOperands32(processor: IA32Processor, imm32: UInt): String =
		getOperands32(processor, imm32.toInt())

	override fun handle32(processor: IA32Processor, imm32: UInt) {
		handle32(processor, imm32.toInt())
	}

	override fun getMnemonic16(processor: IA32Processor, imm16: UShort): String =
		getMnemonic16(processor, imm16.toShort())

	override fun getOperands16(processor: IA32Processor, imm16: UShort): String =
		getOperands16(processor, imm16.toShort())

	override fun handle16(processor: IA32Processor, imm16: UShort) {
		handle16(processor, imm16.toShort())
	}
}