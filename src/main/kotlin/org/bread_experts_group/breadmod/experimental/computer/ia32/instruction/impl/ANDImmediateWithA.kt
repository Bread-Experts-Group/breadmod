package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0x25u)
object ANDImmediateWithA : Instruction("and"), Immediate16, Immediate32, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "eax, ${hex(processor.imm32())}"
		AddressingLength.R16 -> "ax, ${hex(processor.imm16())}"
		AddressingLength.R8  -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val result = processor.a.tex and processor.imm32()
			this.setFlagsForResult(processor, result)
			processor.a.tex = result
		}
		AddressingLength.R16 -> {
			val result = processor.a.tx and processor.imm16()
			this.setFlagsForResult(processor, result)
			processor.a.tx = result
		}
		AddressingLength.R8  -> throw UnsupportedOperationException()
	}
}