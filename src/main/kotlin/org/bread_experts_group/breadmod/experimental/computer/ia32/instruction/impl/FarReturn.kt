package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

@IA32Instruction(0xCBu)
object FarReturn : Instruction("retf") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> hex(processor.pop32())
		AddressingLength.R16 -> hex(processor.pop16())
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			processor.ip.tex = processor.pop32()
			processor.cs.tx = processor.pop32().toUShort()
		}
		AddressingLength.R16 -> {
			processor.ip.tex = processor.pop16().toUInt()
			processor.cs.tx = processor.pop16()
		}
		else                 -> throw UnsupportedOperationException()
	}
}