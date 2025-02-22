package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H89InstructionMOV : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.memRM.decide(
			{ it.set(rm.register.get()) },
			{ processor.computer.setMemoryAt16(it, rm.register.get().toUShort()) }
		)
	}

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.memRM.decide(
			{ it.set(rm.register.get()) },
			{ processor.computer.setMemoryAt32(it, rm.register.get().toUInt()) }
		)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}