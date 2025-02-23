package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H55InstructionPUSH : Instruction {
	override fun getOperands16(processor: IA32Processor): String = "bp [${hex(processor.bp.tx)}]"

	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.bp.tx)
	}

	override fun getOperands32(processor: IA32Processor): String = "ebp [${hex(processor.bp.tex)}]"

	override fun handle32(processor: IA32Processor) {
		processor.push32(processor.bp.tex)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}