package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HF6InstructionTEST : LogicalArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle16(processor: IA32Processor) {
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		if (r != 0u) TODO("NOT, NEG, MUL, IMUL, DIV, IDIV /$r!")
		val imm8 = processor.fetch().let { processor.cir }
		val result = rm.memRM.decide(
			{ it.get() and (imm8.toULong()) },
			{ (processor.computer.requestMemoryAt(it) and imm8).toULong() }
		)

		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}