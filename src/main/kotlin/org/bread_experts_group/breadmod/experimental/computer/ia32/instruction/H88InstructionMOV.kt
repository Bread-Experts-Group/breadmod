package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H88InstructionMOV : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir, DecodingUtil.AddressingLength.R8)
		rm.memRM.decide(
			{ it.set(rm.register.get()) },
			{ processor.computer.setMemoryAt(it, rm.register.get().toUByte()) }
		)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}