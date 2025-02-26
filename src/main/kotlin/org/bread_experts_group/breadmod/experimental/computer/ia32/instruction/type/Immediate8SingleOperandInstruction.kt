package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.Instruction

interface Immediate8SingleOperandInstruction : Instruction {
	override fun handle(processor: IA32Processor) {
		handle(processor, processor.decoding.readFetch())
	}

	override fun getMnemonic(processor: IA32Processor): String {
		val imm8 = processor.decoding.readFetch()
		return getMnemonic(processor, imm8)
	}

	override fun getOperands(processor: IA32Processor): String {
		val imm8 = processor.decoding.readFetch()
		return getOperands(processor, imm8)
	}

	fun getMnemonic(processor: IA32Processor, imm8: UByte): String
	fun getOperands(processor: IA32Processor, imm8: UByte): String
	fun handle(processor: IA32Processor, imm8: UByte)
}