package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Immediate8SingleOperandInstruction

@IA32Instruction(0x3Cu)
object CompareImmediateToAL : Immediate8SingleOperandInstruction, ArithmeticSubtractionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "cmp"
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = "al, ${hex(imm8)}"
	override fun handle(processor: IA32Processor, imm8: UByte) {
		val result = this.setFlagsForOperationR(processor, processor.a.l, imm8)
		this.setFlagsForResult(processor, result)
	}
}