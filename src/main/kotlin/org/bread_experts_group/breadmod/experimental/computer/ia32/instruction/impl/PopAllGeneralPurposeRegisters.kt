package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

@IA32Instruction(0x61u)
object PopAllGeneralPurposeRegisters : Instruction("popa") {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R16 -> {
			processor.di.tx = processor.pop16()
			processor.si.tx = processor.pop16()
			processor.bp.tx = processor.pop16()
			processor.pop16()
			processor.b.tx = processor.pop16()
			processor.d.tx = processor.pop16()
			processor.c.tx = processor.pop16()
			processor.a.tx = processor.pop16()
		}
		AddressingLength.R32 -> {
			processor.di.tex = processor.pop32()
			processor.si.tex = processor.pop32()
			processor.bp.tex = processor.pop32()
			processor.pop32()
			processor.b.tex = processor.pop32()
			processor.d.tex = processor.pop32()
			processor.c.tex = processor.pop32()
			processor.a.tex = processor.pop32()
		}
		else                 -> throw UnsupportedOperationException()
	}
}