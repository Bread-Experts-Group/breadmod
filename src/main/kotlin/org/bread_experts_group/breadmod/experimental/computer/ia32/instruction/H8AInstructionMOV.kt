package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H8AInstructionMOV : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		processor.bit8Override = true
		val (f, s) = processor.decoding.getModRMDisassembler(processor.cir).first
		processor.bit8Override = false
		return "$s, $f"
	}

	override fun handle16(processor: IA32Processor) {
		processor.bit8Override = true
		val (rm) = processor.decoding.getModRM(processor.cir)
		processor.bit8Override = false
		rm.register.set(
			rm.memRM.decide(
				{ it.get() },
				{
					val physical = processor.ds.offset(it)
					processor.computer.requestMemoryAt(physical).toULong()
				}
			)
		)
	}

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)
	override fun handle32(processor: IA32Processor) = handle16(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}