package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface RegisterMemory8SingleOperandInstruction : RegisterMemorySingleOperandInstruction {
	override fun handle(processor: IA32Processor) {
		processor.bit8Override = true
		super.handle(processor)
	}

	override fun getMnemonic(processor: IA32Processor): String {
		processor.bit8Override = true
		return super.getMnemonic(processor)
	}

	override fun getOperands(processor: IA32Processor): String {
		processor.bit8Override = true
		return super.getOperands(processor)
	}
}