package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H84InstructionTEST : LogicalArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		processor.bit8Override = true
		val (m, r) = processor.decoding.getModRMDisassembler(processor.cir).first
		processor.bit8Override = false
		return "$r, $m"
	}

	override fun handle16(processor: IA32Processor) {
		processor.bit8Override = true
		val (rm, _) = processor.decoding.getModRM(processor.cir)
		processor.bit8Override = false
		val result = rm.memRM.decide(
			{ it.get() and rm.register.get() },
			{ (processor.computer.requestMemoryAt(it) and rm.register.get().toUByte()).toULong() }
		)

		this.setFlagsToResult(processor, result)
	}

	override fun handle32(processor: IA32Processor) = handle16(processor)
	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}