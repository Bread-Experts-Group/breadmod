package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction

interface RegisterMemorySelectorSingleOperandGroupInstruction : ZeroOperandInstruction {
	val instructions: Map<UInt, RegisterMemorySingleOperandInstruction>

	private fun getInstruction(processor: IA32Processor): RegisterMemorySingleOperandInstruction {
		val rmByte = processor.decoding.readFetch()
		val ins = this.instructions.getValue(processor.decoding.getComponents(rmByte).second)
		processor.ip.rx -= 1u
		return ins
	}

	override fun handle(processor: IA32Processor): Unit = this.getInstruction(processor).handle(processor)
	override fun getMnemonic(processor: IA32Processor): String = this.getInstruction(processor).getMnemonic(processor)
	override fun getOperands(processor: IA32Processor): String = this.getInstruction(processor).getOperands(processor)
}