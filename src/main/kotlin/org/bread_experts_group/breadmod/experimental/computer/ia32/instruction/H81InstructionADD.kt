package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H81InstructionADD : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands32(processor: IA32Processor): String {
		prepare(processor)
		val (f, _) = processor.decoding.getModRMDisassembler(processor.cir).first
		// TODO consider reg
		return "$f, ${hex(processor.decoding.readBinaryI(4).toUInt())}"
	}

	override fun handle32(processor: IA32Processor) {
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		if (r != 0u) TODO("OR, ADC, SBB, AND, SUB, XOR, CMP /$r!")
		val imm = processor.decoding.readBinaryI(4).toULong()
		val result = rm.memRM.decide(
			{ (it.get() + imm).also(it::set) },
			{
				(processor.computer.requestMemoryAt32(it) + imm).also { r ->
					processor.computer.setMemoryAt32(it, r.toUInt())
				}
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}