package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H39InstructionCMP : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		val result = rm.register.get() - rm.memRM.decide(
			{ it.get() },
			{ processor.computer.requestMemoryAt16(it).toULong() }
		)
		this.setFlagsToResult(processor, result)
	}

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		val result = rm.register.get() - rm.memRM.decide(
			{ it.get() },
			{ processor.computer.requestMemoryAt32(it).toULong() }
		)
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}