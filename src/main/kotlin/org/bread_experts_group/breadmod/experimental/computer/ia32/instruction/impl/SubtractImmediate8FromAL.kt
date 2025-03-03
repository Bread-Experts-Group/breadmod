package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8

@IA32Instruction(0x2Cu)
object SubtractImmediate8FromAL : Instruction("sub"), Immediate8, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = "al, ${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val result = this.setFlagsForOperationR(processor, processor.a.tl, processor.imm8())
		processor.a.tl = result
		this.setFlagsForResult(processor, result)
	}
}