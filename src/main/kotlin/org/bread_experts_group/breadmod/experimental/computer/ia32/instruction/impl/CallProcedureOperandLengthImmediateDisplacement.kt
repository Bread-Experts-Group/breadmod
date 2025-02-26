package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateSignedOperatingLengthSingleOperandInstruction

/**
 * Opcode: `E8 c(w/d)` |
 * Instruction: `CALL rel(16/32)` |
 * Flags Modified: `none`
 * TODO: Please see the operation listing for CALL on the manual
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0xE8u)
object CallProcedureOperandLengthImmediateDisplacement : ImmediateSignedOperatingLengthSingleOperandInstruction {
	override fun getMnemonic32(processor: IA32Processor, rel32: Int): String = "call"
	override fun getOperands32(processor: IA32Processor, rel32: Int): String =
		"${hex(rel32)} [${hex((processor.ip.tex.toInt() + rel32).toUInt())}]"

	override fun handle32(processor: IA32Processor, rel32: Int) {
		processor.push32(processor.ip.tex)
		processor.ip.tex = (processor.ip.tex.toInt() + rel32).toUInt()
	}

	override fun getMnemonic16(processor: IA32Processor, rel16: Short): String = "call"
	override fun getOperands16(processor: IA32Processor, rel16: Short): String =
		"${hex(rel16)} [${hex((processor.ip.tex.toInt() + rel16).toUShort().toUInt())}]"

	override fun handle16(processor: IA32Processor, rel16: Short) {
		processor.push16(processor.ip.tx)
		processor.ip.tex = (processor.ip.tex.toInt() + rel16).toUShort().toUInt()
	}
}