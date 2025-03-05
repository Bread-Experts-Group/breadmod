package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0xA1u)
object MoveSegmentOffsetToA : Instruction("mov"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String {
		val operand = when (processor.operandSize) {
			AddressingLength.R32 -> "eax"
			AddressingLength.R16 -> "ax"
			else                 -> throw UnsupportedOperationException()
		}
		val address = when (processor.addressSize) {
			AddressingLength.R32 -> processor.imm32().let {
				"${processor.segment.name}:[${hex(it)}] [${hex(processor.segment.offset(it.toULong()))}]"
			}
			AddressingLength.R16 -> processor.imm16().let {
				"${processor.segment.name}:[${hex(it)}] [${hex(processor.segment.offset(it.toULong()))}]"
			}
			else                 -> throw UnsupportedOperationException()
		}
		return "$operand, $address"
	}

	override fun handle(processor: IA32Processor) {
		val address = when (processor.addressSize) {
			AddressingLength.R32 -> processor.segment.offset(processor.imm32().toULong())
			AddressingLength.R16 -> processor.segment.offset(processor.imm16().toULong())
			else                 -> throw UnsupportedOperationException()
		}
		when (processor.operandSize) {
			AddressingLength.R32 -> processor.a.tex = processor.computer.requestMemoryAt32(address)
			AddressingLength.R16 -> processor.a.tx = processor.computer.requestMemoryAt16(address)
			else                 -> throw UnsupportedOperationException()
		}
	}
}