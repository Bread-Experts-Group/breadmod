package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

/**
 * Opcode: `E8 c(w/d)` |
 * Instruction: `CALL rel(16/32)` |
 * Flags Modified: `none`
 * TODO: Please see the operation listing for CALL on the manual
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0xE8u)
object CallProcedureOperandLengthImmediateDisplacement : Instruction("call"), Immediate16, Immediate32 {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> processor.rel32()
			.let { "${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]" }
		AddressingLength.R16 -> processor.rel16()
			.let { "${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUShort())}]" }
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val rel32 = processor.rel32()
			processor.push32(processor.ip.tex)
			processor.ip.tex = (processor.ip.tex.toInt() + rel32).toUInt()
		}
		AddressingLength.R16 -> {
			val rel16 = processor.rel16()
			processor.push16(processor.ip.tx)
			processor.ip.tex = ((processor.ip.tex.toInt() + rel16).toUShort()).toUInt()
		}
		else                 -> throw UnsupportedOperationException()
	}
}