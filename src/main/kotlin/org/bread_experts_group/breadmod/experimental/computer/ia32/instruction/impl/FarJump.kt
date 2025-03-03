package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0xEAu)
object FarJump : Instruction("ljmp"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> hex(processor.imm32()).substring(2).let { "${hex(processor.imm16())}:$it" }
		AddressingLength.R16 -> hex(processor.imm16()).substring(2).let { "${hex(processor.imm16())}:$it" }
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor) {
		val (ip, cs) = when (processor.operandSize) {
			AddressingLength.R32 -> processor.imm32() to processor.imm16()
			AddressingLength.R16 -> processor.imm16().toUInt() to processor.imm16()
			else                 -> throw UnsupportedOperationException()
		}
		processor.ip.tex = ip
		processor.cs.tx = cs
	}
}