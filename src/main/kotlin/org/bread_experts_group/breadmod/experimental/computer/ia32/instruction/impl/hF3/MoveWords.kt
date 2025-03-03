package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

@IA32Instruction(0xF3ABu)
object MoveWords : Instruction("rep stos") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 ->
			"es:[edi] -> es:[edi [${hex(processor.es.offset(processor.di.ex))}] + ecx [${hex(processor.c.tex)}]], " +
					"eax [${hex(processor.a.tex)}]"
		AddressingLength.R16 ->
			"es:[di] -> es:[di [${hex(processor.es.offset(processor.di.x))}] + cx [${hex(processor.c.tx)}]], " +
					"ax [${hex(processor.a.tx)}]"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> while (processor.c.ex > 0u) {
			processor.c.ex--
			processor.computer.setMemoryAt32(processor.es.offset(processor.di), processor.a.tex)
			processor.di.ex += 4u
		}
		AddressingLength.R16 -> while (processor.c.x > 0u) {
			processor.c.x--
			processor.computer.setMemoryAt16(processor.es.offset(processor.di), processor.a.tx)
			processor.di.x += 2u
		}
		else                 -> throw UnsupportedOperationException()
	}
}