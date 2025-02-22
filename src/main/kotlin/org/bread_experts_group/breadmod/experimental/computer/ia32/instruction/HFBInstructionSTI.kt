package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HFBInstructionSTI : Instruction {
	fun handle(processor: IA32Processor) {
		processor.setFlag(IA32Processor.FLAGSFlagType.INTERRUPT_ENABLE_FLAG, true)
	}

	override fun handle16(processor: IA32Processor) = handle(processor)
	override fun handle32(processor: IA32Processor) = handle(processor)
	override fun handle64(processor: IA32Processor) = handle(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}