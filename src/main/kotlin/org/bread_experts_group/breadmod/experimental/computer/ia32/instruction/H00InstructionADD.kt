package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H00InstructionADD : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		val (f, s) = processor.decoding.getModRMDisassembler(processor.cir).first
		return "$f, $s"
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		val result = rm.memRM.decide(
			{ (it.get() + rm.register.get()).also(it::set) },
			{
				(processor.computer.requestMemoryAt(it) + rm.register.get()).also { r ->
					processor.computer.setMemoryAt(it, r.toUByte())
				}
			}
		)
		setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}