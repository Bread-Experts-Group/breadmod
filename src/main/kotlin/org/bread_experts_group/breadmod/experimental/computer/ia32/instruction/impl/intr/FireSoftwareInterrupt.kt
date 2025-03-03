package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8

@IA32Instruction(0xCDu)
object FireSoftwareInterrupt : Instruction("int"), Immediate8 {
	override fun operands(processor: IA32Processor): String = hex(processor.imm8())
	override fun handle(processor: IA32Processor): Unit = processor.initiateInterrupt(processor.imm8())
}