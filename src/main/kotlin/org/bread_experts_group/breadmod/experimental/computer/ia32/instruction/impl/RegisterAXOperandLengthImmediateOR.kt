package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateOperatingLengthSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations

/**
 * Opcode: `0D i(w/d)` |
 * Instruction: `OR (E)AX, imm(16/32)` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x0Du)
object RegisterAXOperandLengthImmediateOR :
	ImmediateOperatingLengthSingleOperandInstruction,
	LogicalArithmeticFlagOperations {
	override fun getMnemonic32(processor: IA32Processor, imm32: UInt): String = "or"
	override fun getOperands32(processor: IA32Processor, imm32: UInt): String = "eax, ${hex(imm32)}"
	override fun handle32(processor: IA32Processor, imm32: UInt) {
		val result = processor.a.tex or imm32
		processor.a.tex = result
		this.setFlagsForResult(processor, result)
	}

	override fun getMnemonic16(processor: IA32Processor, imm16: UShort): String = "or"
	override fun getOperands16(processor: IA32Processor, imm16: UShort): String = "ax, ${hex(imm16)}"
	override fun handle16(processor: IA32Processor, imm16: UShort) {
		val result = processor.a.tx or imm16
		processor.a.tx = result
		this.setFlagsForResult(processor, result)
	}
}