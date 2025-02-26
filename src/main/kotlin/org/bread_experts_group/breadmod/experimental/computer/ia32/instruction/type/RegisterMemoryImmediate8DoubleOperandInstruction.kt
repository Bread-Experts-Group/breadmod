package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import kotlin.reflect.KMutableProperty0

interface RegisterMemoryImmediate8DoubleOperandInstruction : RegisterMemorySingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		this.getMnemonic(processor, rm, rmD, processor.decoding.readFetch())

	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		this.getOperands(processor, rm, rmD, processor.decoding.readFetch())

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>): Unit =
		this.handle(processor, rmM, rmR, processor.decoding.readFetch())

	fun getMnemonic(
		processor: IA32Processor,
		rm: ModRMResult, rmD: ModRMDisassemblyResult,
		imm8: UByte
	): String

	fun getOperands(
		processor: IA32Processor,
		rm: ModRMResult, rmD: ModRMDisassemblyResult,
		imm8: UByte
	): String

	fun handle(
		processor: IA32Processor,
		rmM: MemRMResult, rmD: KMutableProperty0<ULong>,
		imm8: UByte
	)
}