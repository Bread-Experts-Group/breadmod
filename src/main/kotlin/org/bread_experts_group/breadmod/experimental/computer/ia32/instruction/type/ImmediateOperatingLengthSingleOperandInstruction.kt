package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import kotlin.reflect.jvm.jvmName

interface ImmediateOperatingLengthSingleOperandInstruction : ZeroOperandInstruction {
	override fun handle(processor: IA32Processor) {
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> this.handle32(processor, imm)
			is UShort -> this.handle16(processor, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}
	}

	override fun getOperands(processor: IA32Processor): String =
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> this.getOperands32(processor, imm)
			is UShort -> this.getOperands16(processor, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}

	fun getOperands32(processor: IA32Processor, imm32: UInt): String
	fun handle32(processor: IA32Processor, imm32: UInt)
	fun getOperands16(processor: IA32Processor, imm16: UShort): String
	fun handle16(processor: IA32Processor, imm16: UShort)
}