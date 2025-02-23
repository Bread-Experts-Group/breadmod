package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H3CInstructionCMP : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		return "al, ${hex(processor.cir)}"
	}

	override fun handle16(processor: IA32Processor) {
		val result = (processor.a.tl - processor.cir).toULong()
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}