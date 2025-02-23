package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H28InstructionSUB : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		processor.bit8Override = true
		val (m, r) = processor.decoding.getModRMDisassembler(processor.cir).first
		processor.bit8Override = false
		return "$m, $r"
	}

	override fun handle16(processor: IA32Processor) {
		processor.bit8Override = true
		val (rm, _) = processor.decoding.getModRM(processor.cir)
		processor.bit8Override = false
		val result = rm.memRM.decide(
			{ (it.get() - rm.register.get()).also(it::set) },
			{
				(processor.computer.requestMemoryAt(it).toULong() - rm.register.get()).also { r ->
					processor.computer.setMemoryAt(it, r.toUByte())
				}.toULong()
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)
	override fun handle32(processor: IA32Processor) = handle16(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}