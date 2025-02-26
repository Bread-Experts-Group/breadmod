package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction

interface Immediate8SingleOperandInstruction : ZeroOperandInstruction {
	override fun handle(processor: IA32Processor) {
		this.handle(processor, processor.decoding.readFetch())
	}

	override fun getOperands(processor: IA32Processor): String {
		val imm8 = processor.decoding.readFetch()
		return this.getOperands(processor, imm8)
	}

	fun getOperands(processor: IA32Processor, imm8: UByte): String
	fun handle(processor: IA32Processor, imm8: UByte)
}