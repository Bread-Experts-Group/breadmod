package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H0F20InstructionMOVCR : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) = this.handle32(processor)

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.register.set(processor.cr0.ex)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}