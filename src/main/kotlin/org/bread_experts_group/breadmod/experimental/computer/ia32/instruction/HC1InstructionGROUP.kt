package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HC1InstructionGROUP : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
		processor.push16(processor.cir.toUShort())
		processor.fetch()
	}

	private fun operation(r: UInt) = when (r) {
		0u -> TODO("ROL")
		1u -> TODO("ROR")
		2u -> TODO("RCL")
		3u -> TODO("RCR")
		4u -> ULong::shl
		5u -> ULong::shr
		6u -> TODO("SHL")
		7u -> TODO("SAR")
		else -> throw IllegalStateException("/$r")
	}

	override fun handle16(processor: IA32Processor) {
		val shfAmt = processor.cir.toInt()
		val (rm, r) = processor.decoding.getModRM(processor.pop16().toUByte())
		val op = operation(r)
		val result = rm.memRM.decide(
			{ op(it.get(), shfAmt).also(it::set) },
			{
				op(processor.computer.requestMemoryAt16(it).toULong(), shfAmt).toUShort().also { r ->
					processor.computer.setMemoryAt16(it, r)
				}.toULong()
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override fun handle32(processor: IA32Processor) {
		val shfAmt = processor.cir.toInt()
		val (rm, r) = processor.decoding.getModRM(processor.pop16().toUByte())
		val op = operation(r)
		val result = rm.memRM.decide(
			{ op(it.get(), shfAmt).also(it::set) },
			{
				op(processor.computer.requestMemoryAt32(it).toULong(), shfAmt).also { r ->
					processor.computer.setMemoryAt32(it, r.toUInt())
				}
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}