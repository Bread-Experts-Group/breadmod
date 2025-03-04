package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0x9Au)
object CallFarProcedureInOperand : Instruction("callf"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String {
		TODO("Not yet implemented")
	}

	override fun handle(processor: IA32Processor) {
		TODO("Not yet implemented")
	}
}