package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HFEInstructionGROUP : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	private fun operation(r: UInt): (ULong, ULong) -> ULong = when (r) {
		0u   -> ULong::plus
		1u   -> ULong::minus
		else -> throw IllegalStateException("/$r")
	}

	override fun getMnemonic(processor: IA32Processor): String {
		prepare(processor)
		val (_, r) = processor.decoding.getModRM(processor.cir)
		val m = when (r) {
			0u   -> "inc"
			1u   -> "dec"
			else -> throw IllegalStateException("/$r")
		}
		return m
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		processor.bit8Override = true
		val (m, _) = processor.decoding.getModRMDisassembler(processor.cir).first
		processor.bit8Override = false
		return m
	}

	override fun handle16(processor: IA32Processor) {
		processor.bit8Override = true
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		processor.bit8Override = false
		val op = operation(r)
		val result = rm.memRM.decide(
			{ op(it.get(), 1u).also(it::set) },
			{
				op(processor.computer.requestMemoryAt(it).toULong(), 1u).also { r ->
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