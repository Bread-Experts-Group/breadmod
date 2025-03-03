package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

/**
 * Opcode: `0D i(w/d)` |
 * Instruction: `OR (E)AX, imm(16/32)` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x0Du)
object RegisterAXOperandLengthImmediateOR : Instruction("or"), Immediate16, Immediate32,
	LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "eax, ${hex(processor.imm32())}"
		AddressingLength.R16 -> "ax, ${hex(processor.imm16())}"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val result = processor.a.tex or processor.imm32()
			processor.a.tex = result
			this.setFlagsForResult(processor, result)
		}
		AddressingLength.R16 -> {
			val result = processor.a.tx or processor.imm16()
			processor.a.tx = result
			this.setFlagsForResult(processor, result)
		}
		else                 -> throw UnsupportedOperationException()
	}
}