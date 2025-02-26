package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.Instruction

interface RegisterMemorySelectorSingleOperandGroupInstruction : Instruction {
	val instructions: Map<UInt, RegisterMemorySingleOperandInstruction>

	override fun handle(processor: IA32Processor) {
		val rmByte = processor.decoding.readFetch()
		val ins = this.instructions.getValue(processor.decoding.getComponents(rmByte).second)
		processor.ip.rx -= 1u
		ins.handle(processor)
	}

	override fun getMnemonic(processor: IA32Processor): String {
		val rmByte = processor.decoding.readFetch()
		val ins = this.instructions.getValue(processor.decoding.getComponents(rmByte).second)
		processor.ip.rx -= 1u
		return ins.getMnemonic(processor)
	}

	override fun getOperands(processor: IA32Processor): String {
		val rmByte = processor.decoding.readFetch()
		val ins = this.instructions.getValue(processor.decoding.getComponents(rmByte).second)
		processor.ip.rx -= 1u
		return ins.getOperands(processor)
	}
}