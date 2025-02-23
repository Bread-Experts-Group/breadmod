package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H53InstructionPUSH : Instruction {
	override fun getOperands16(processor: IA32Processor): String = "bx [${hex(processor.b.tx)}]"

	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.b.tx)
	}

	override fun getOperands32(processor: IA32Processor): String = "ebx [${hex(processor.b.tex)}]"

	override fun handle32(processor: IA32Processor) {
		processor.push32(processor.b.tex)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}