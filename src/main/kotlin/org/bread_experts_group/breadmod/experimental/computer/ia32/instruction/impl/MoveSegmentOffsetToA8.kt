package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32

@IA32Instruction(0xA0u)
object MoveSegmentOffsetToA8 : Instruction("mov"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "al, [${hex(processor.segmentOverride.offset(processor.imm32().toULong()))}]"
		AddressingLength.R16 -> "al, [${hex(processor.segmentOverride.offset(processor.imm16().toULong()))}]"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			processor.a.tl = processor.computer.requestMemoryAt(
				processor.segmentOverride.offset(processor.imm32().toULong())
			)
		}
		AddressingLength.R16 -> {
			processor.a.tl = processor.computer.requestMemoryAt(
				processor.segmentOverride.offset(processor.imm16().toULong())
			)
		}
		else                 -> throw UnsupportedOperationException()
	}
}