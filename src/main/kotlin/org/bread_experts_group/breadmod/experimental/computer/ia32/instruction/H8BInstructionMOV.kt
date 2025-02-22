package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H8BInstructionMOV : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.register.set(
			rm.memRM.decide(
				{ it.get() },
				{
					val physical = (if (processor.csOverride) processor.cs else processor.ds).offset(it)
					processor.computer.requestMemoryAt16(physical).toULong()
				}
			)
		)
	}

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.register.set(
			rm.memRM.decide(
				{ it.get() },
				{
					val physical = (if (processor.csOverride) processor.cs else processor.ds).offset(it)
					processor.computer.requestMemoryAt32(physical).toULong()
				}
			)
		)
	}

	override val supportsCodeSegmentOverride: Boolean = true
}