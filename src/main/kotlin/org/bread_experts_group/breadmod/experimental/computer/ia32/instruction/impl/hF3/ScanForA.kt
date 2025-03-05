package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

@IA32Instruction(0xF2FAu)
object ScanForA : Instruction("repne scas") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "eax [${hex(processor.a.tex)}], es:[edi [${hex(processor.di.tex)}]]"
		AddressingLength.R16 -> "ax [${hex(processor.a.tx)}], es:[di [${hex(processor.di.tx)}]]"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor) {
		TODO("Not yet implemented")
	}
}