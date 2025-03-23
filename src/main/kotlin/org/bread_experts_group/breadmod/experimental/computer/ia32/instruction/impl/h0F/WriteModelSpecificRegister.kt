package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

@IA32Instruction(0x0F30u)
object WriteModelSpecificRegister : Instruction("wrmsr") {
	override fun operands(processor: IA32Processor): String {
		return "TODO MSR"
	}

	override fun handle(processor: IA32Processor) {
		// TODO.
	}
}