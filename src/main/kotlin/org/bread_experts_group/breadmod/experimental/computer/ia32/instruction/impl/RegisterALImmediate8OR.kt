package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Immediate8SingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations

/**
 * Opcode: `0C ib` |
 * Instruction: `OR AL, imm8` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x0Cu)
object RegisterALImmediate8OR : Immediate8SingleOperandInstruction, LogicalArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor, imm8: UByte): String = "or"
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = "al, ${hex(imm8)}"
	override fun handle(processor: IA32Processor, imm8: UByte) {
		val result = processor.a.tl or imm8
		processor.a.tl = result
		this.setFlagsForResult(processor, result)
	}
}