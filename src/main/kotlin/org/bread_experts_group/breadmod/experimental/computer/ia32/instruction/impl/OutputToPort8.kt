package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Immediate8SingleOperandInstruction

@IA32Instruction(0xE6u)
object OutputToPort8 : Immediate8SingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, imm8: UByte): String = "out"
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = "${hex(imm8)}, al"
	override fun handle(processor: IA32Processor, imm8: UByte) {
		processor.computer.ioMap.getValue(imm8.toUInt()).write(processor.a.tl)
	}
}