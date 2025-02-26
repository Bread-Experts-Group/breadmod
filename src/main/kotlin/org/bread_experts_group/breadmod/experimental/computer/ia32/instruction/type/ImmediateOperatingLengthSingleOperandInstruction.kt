package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.Instruction
import kotlin.reflect.jvm.jvmName

interface ImmediateOperatingLengthSingleOperandInstruction : Instruction {
	override fun handle(processor: IA32Processor) {
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> handle32(processor, imm)
			is UShort -> handle16(processor, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}
	}

	override fun getMnemonic(processor: IA32Processor): String =
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> getMnemonic32(processor, imm)
			is UShort -> getMnemonic16(processor, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}

	override fun getOperands(processor: IA32Processor): String =
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> getOperands32(processor, imm)
			is UShort -> getOperands16(processor, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}

	fun getMnemonic32(processor: IA32Processor, imm32: UInt): String
	fun getOperands32(processor: IA32Processor, imm32: UInt): String
	fun handle32(processor: IA32Processor, imm32: UInt)
	fun getMnemonic16(processor: IA32Processor, imm16: UShort): String
	fun getOperands16(processor: IA32Processor, imm16: UShort): String
	fun handle16(processor: IA32Processor, imm16: UShort)
}