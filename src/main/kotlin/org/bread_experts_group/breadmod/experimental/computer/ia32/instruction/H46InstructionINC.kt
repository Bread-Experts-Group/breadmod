package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H46InstructionINC : Instruction {
	override fun getOperands16(processor: IA32Processor): String = "si"
	override fun handle16(processor: IA32Processor) {
		processor.si.x++
	}

	override fun getOperands32(processor: IA32Processor): String = "esi"
	override fun handle32(processor: IA32Processor) {
		processor.si.ex++
	}

	override val supportsCodeSegmentOverride: Boolean = false
}