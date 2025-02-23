package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H6AInstructionPUSH : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		hex(processor.fetch().let { processor.cir.toUShort() })

	override fun handle16(processor: IA32Processor) {
		processor.fetch()
		processor.push16(processor.cir.toUShort())
	}

	override val supportsCodeSegmentOverride: Boolean = false
}