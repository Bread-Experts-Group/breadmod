package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Immediate8SingleOperandOperatingLengthDependentInstruction

@IA32Instruction(0x6Au)
object PushImmediate8OnStack : Immediate8SingleOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "push"
	override fun getOperands16(processor: IA32Processor, imm8: UByte): String = hex(imm8)
	override fun getOperands32(processor: IA32Processor, imm8: UByte): String = hex(imm8)
	override fun handle16(processor: IA32Processor, imm8: UByte): Unit = processor.push16(imm8.toUShort())
	override fun handle32(processor: IA32Processor, imm8: UByte): Unit = processor.push32(imm8.toUInt())
}