package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction

@IA32Instruction(0xF3ABu)
object MoveWords : ZeroOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "rep stos"
	override fun getOperands16(processor: IA32Processor): String =
		"es:[di] -> es:[di [${hex(processor.es.offset(processor.di.x))}] + cx [${hex(processor.c.tx)}]], " +
				"ax [${hex(processor.a.tx)}]"

	override fun getOperands32(processor: IA32Processor): String =
		"es:[edi] -> es:[edi [${hex(processor.di.tex)}] + ecx [${hex(processor.c.tex)}]], " +
				"eax [${hex(processor.a.tex)}]"

	override fun handle16(processor: IA32Processor) {
		while (processor.c.x > 0u) {
			processor.c.x--
			processor.computer.setMemoryAt16(processor.es.offset(processor.di), processor.a.tx)
			processor.di.x += 2u
		}
	}

	override fun handle32(processor: IA32Processor) {
		while (processor.c.ex > 0u) {
			processor.c.ex--
			processor.computer.setMemoryAt32(processor.es.offset(processor.di), processor.a.tex)
			processor.di.ex += 4u
		}
	}
}