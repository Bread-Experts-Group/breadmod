package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HD1InstructionSHR : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		if (r != 5u) TODO("ROL, ROR, RCL, RCR, SHL, SAL, SAR /$r!")
		val result = rm.memRM.decide(
			{ (it.get() shr 1).also(it::set) },
			{
				(processor.computer.requestMemoryAt16(it).toULong() shr 1).also { r ->
					processor.computer.setMemoryAt16(it, r.toUShort())
				}
			}
		)

		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}