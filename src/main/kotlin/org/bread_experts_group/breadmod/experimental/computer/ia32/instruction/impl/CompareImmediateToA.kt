package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0x3Du)
object CompareImmediateToA : Instruction("cmp"), Immediate16, Immediate32, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "ax, ${hex(processor.imm16())}"
		AddressingLength.R16 -> "eax, ${hex(processor.imm32())}"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val result = this.setFlagsForOperationR(processor, processor.a.tex, processor.imm32())
			this.setFlagsForResult(processor, result)
		}
		AddressingLength.R16 -> {
			val result = this.setFlagsForOperationR(processor, processor.a.tx, processor.imm16())
			this.setFlagsForResult(processor, result)
		}
		else                 -> throw UnsupportedOperationException()
	}
}