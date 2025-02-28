package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import kotlin.reflect.jvm.jvmName

interface ImmediateSegmentAndOffsetOperatingLengthDoubleOperandInstruction : ZeroOperandInstruction {
	override fun handle(processor: IA32Processor) {
		val imm = processor.decoding.readBinaryForMode()
		val seg16 = processor.decoding.readBinaryFetch(2).toUShort()
		when (imm) {
			is UInt   -> this.handle32(processor, seg16, imm)
			is UShort -> this.handle16(processor, seg16, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}
	}

	override fun getOperands(processor: IA32Processor): String {
		val imm = processor.decoding.readBinaryForMode()
		val seg16 = processor.decoding.readBinaryFetch(2).toUShort()
		return when (imm) {
			is UInt   -> this.getOperands32(processor, seg16, imm)
			is UShort -> this.getOperands16(processor, seg16, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}
	}

	fun getOperands32(processor: IA32Processor, seg16: UShort, imm32: UInt): String
	fun handle32(processor: IA32Processor, seg16: UShort, imm32: UInt)
	fun getOperands16(processor: IA32Processor, seg16: UShort, imm16: UShort): String
	fun handle16(processor: IA32Processor, seg16: UShort, imm16: UShort)
}