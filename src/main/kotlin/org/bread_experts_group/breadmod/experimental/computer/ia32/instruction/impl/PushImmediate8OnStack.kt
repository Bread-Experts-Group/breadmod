package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Immediate8SingleOperandInstruction

@IA32Instruction(0x6Au)
object PushImmediate8OnStack : Immediate8SingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "push"
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = hex(imm8)
	override fun handle(processor: IA32Processor, imm8: UByte) {
		processor.push16(imm8.toUShort())
	}
}