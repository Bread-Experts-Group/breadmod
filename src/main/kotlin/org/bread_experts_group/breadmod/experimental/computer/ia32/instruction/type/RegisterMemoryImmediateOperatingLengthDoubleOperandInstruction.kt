package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.jvmName

interface RegisterMemoryImmediateOperatingLengthDoubleOperandInstruction : RegisterMemorySingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> this.getMnemonic32(processor, rm, rmD, imm)
			is UShort -> this.getMnemonic16(processor, rm, rmD, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}

	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> this.getOperands32(processor, rm, rmD, imm)
			is UShort -> this.getOperands16(processor, rm, rmD, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>): Unit =
		when (val imm = processor.decoding.readBinaryForMode()) {
			is UInt   -> this.handle32(processor, rmM, rmR, imm)
			is UShort -> this.handle16(processor, rmM, rmR, imm)
			else      -> throw IllegalArgumentException("Cannot support ${imm::class.jvmName}")
		}

	fun getMnemonic16(
		processor: IA32Processor,
		rm: ModRMResult, rmD: ModRMDisassemblyResult,
		imm16: UShort
	): String

	fun getMnemonic32(
		processor: IA32Processor,
		rm: ModRMResult, rmD: ModRMDisassemblyResult,
		imm32: UInt
	): String

	fun getOperands16(
		processor: IA32Processor,
		rm: ModRMResult, rmD: ModRMDisassemblyResult,
		imm16: UShort
	): String

	fun getOperands32(
		processor: IA32Processor,
		rm: ModRMResult, rmD: ModRMDisassemblyResult,
		imm32: UInt
	): String

	fun handle16(
		processor: IA32Processor,
		rmM: MemRMResult, rmD: KMutableProperty0<ULong>,
		imm16: UShort
	)

	fun handle32(
		processor: IA32Processor,
		rmM: MemRMResult, rmD: KMutableProperty0<ULong>,
		imm32: UInt
	)
}