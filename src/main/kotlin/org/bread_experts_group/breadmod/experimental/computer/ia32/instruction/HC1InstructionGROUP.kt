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
		4u -> ULong::shl to "shl"
		5u -> ULong::shr to "shr"
		6u -> TODO("SHL")
		7u -> TODO("SAR")
		else -> throw IllegalStateException("/$r")
	}

	override fun getMnemonic(processor: IA32Processor): String {
		prepare(processor)
		val (_, r) = processor.decoding.getModRM(processor.pop16().toUByte())
		val (_, m) = operation(r)
		return m
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		val shfAmt = processor.cir.toInt()
		val (m, _) = processor.decoding.getModRMDisassembler(processor.pop16().toUByte()).first
		return "$m, $shfAmt"
	}

	override fun handle16(processor: IA32Processor) {
		val shfAmt = processor.cir.toInt()
		val (rm, r) = processor.decoding.getModRM(processor.pop16().toUByte())
		val (op) = operation(r)
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

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)

	override fun handle32(processor: IA32Processor) {
		val shfAmt = processor.cir.toInt()
		val (rm, r) = processor.decoding.getModRM(processor.pop16().toUByte())
		val (op) = operation(r)
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