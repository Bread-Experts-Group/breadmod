package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HC3InstructionRET : Instruction {
	override fun getOperands16(processor: IA32Processor): String = hex(processor.pop16())

	override fun handle16(processor: IA32Processor) {
		val reset = processor.pop16()
		processor.ip.tex = reset.toUInt()
	}

	override fun getOperands32(processor: IA32Processor): String = hex(processor.pop32())

	override fun handle32(processor: IA32Processor) {
		val reset = processor.pop32()
		processor.ip.tex = reset.toUInt()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}