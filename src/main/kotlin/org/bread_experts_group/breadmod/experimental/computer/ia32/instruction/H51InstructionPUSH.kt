package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H51InstructionPUSH : Instruction {
	override fun getOperands16(processor: IA32Processor): String = "cx [${hex(processor.c.tx)}]"

	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.c.tx)
	}

	override fun getOperands32(processor: IA32Processor): String = "ecx [${hex(processor.c.tex)}]"

	override fun handle32(processor: IA32Processor) {
		processor.push32(processor.c.tex)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}