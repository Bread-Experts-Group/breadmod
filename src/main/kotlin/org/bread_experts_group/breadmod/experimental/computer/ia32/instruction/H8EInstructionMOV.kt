package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H8EInstructionMOV : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val rm = processor.decoding.getModRMSreg(processor.cir)
		rm.memRM.decide(
			{ rm.register.set(it.get()) },
			{ rm.register.set(processor.computer.requestMemoryAt16(processor.ds.offset(it)).toULong()) }
		)
	}

	override fun handle32(processor: IA32Processor) {
		val rm = processor.decoding.getModRMSreg(processor.cir)
		rm.memRM.decide(
			{ rm.register.set(it.get()) },
			{ rm.register.set(processor.computer.requestMemoryAt16(processor.ds.offset(it)).toULong()) }
		)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}