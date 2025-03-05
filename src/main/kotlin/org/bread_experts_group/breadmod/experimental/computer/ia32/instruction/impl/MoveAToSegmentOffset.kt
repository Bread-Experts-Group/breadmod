package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0xA3u)
object MoveAToSegmentOffset : Instruction("mov"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> processor.imm32().let {
			"${processor.segment.name}:[${hex(it)}] [${hex(processor.segment.offset(it.toULong()))}], eax"
		}
		AddressingLength.R16 -> processor.imm16().let {
			"${processor.segment.name}:[${hex(it)}] [${hex(processor.segment.offset(it.toULong()))}], ax"
		}
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> processor.imm32().let {
			processor.computer.setMemoryAt32(processor.segment.offset(it.toULong()), processor.a.tex)
		}
		AddressingLength.R16 -> processor.imm16().let {
			processor.computer.setMemoryAt16(processor.segment.offset(it.toULong()), processor.a.tx)
		}
		else                 -> throw UnsupportedOperationException()
	}
}