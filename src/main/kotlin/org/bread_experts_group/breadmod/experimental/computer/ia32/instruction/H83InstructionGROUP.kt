package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H83InstructionGROUP : ArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	private fun operation(r: UInt): (ULong, ULong) -> ULong = when (r) {
		0u -> ULong::plus
		1u -> ULong::or
		2u -> TODO("ADC")
		3u -> TODO("SBB")
		4u -> ULong::and
		5u -> TODO("SUB")
		6u -> TODO("XOR")
		7u -> TODO("CMP")
		else -> throw IllegalStateException("/$r")
	}

	override fun getMnemonic(processor: IA32Processor): String {
		prepare(processor)
		val (_, r) = processor.decoding.getModRM(processor.cir)
		val m = when (r) {
			0u   -> "add"
			1u   -> "or"
			4u -> "and"
			else -> throw IllegalStateException("/$r")
		}
		return m
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		val (m, _) = processor.decoding.getModRMDisassembler(processor.cir).first
		val imm8 = processor.decoding.readBinaryI(1).toUByte()
		return "$m, ${hex(imm8)}"
	}

	override fun handle16(processor: IA32Processor) {
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		val op = operation(r)
		val imm = processor.decoding.readBinaryI(1).toULong()
		val result = rm.memRM.decide(
			{ op(it.get(), imm).also(it::set) },
			{
				op(processor.computer.requestMemoryAt16(it).toULong(), imm).also { r ->
					processor.computer.setMemoryAt16(it, r.toUShort())
				}.toULong()
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)
	override fun handle32(processor: IA32Processor) {
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		val op = operation(r)
		val imm = processor.decoding.readBinaryI(1).toULong()
		val result = rm.memRM.decide(
			{ op(it.get(), imm).also(it::set) },
			{
				op(processor.computer.requestMemoryAt32(it).toULong(), imm).also { r ->
					processor.computer.setMemoryAt32(it, r.toUInt())
				}.toULong()
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}