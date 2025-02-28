package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import kotlin.reflect.KMutableProperty0

interface RegisterMemoryOperatingLengthDependentSingleOperandInstruction : RegisterMemorySingleOperandInstruction {
	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>): Unit =
		when (processor.operatingModeLocal) {
			AddressingLength.R32 -> this.handle32(processor, rmM, rmR)
			AddressingLength.R16 -> this.handle16(processor, rmM, rmR)
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}

	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		when (processor.operatingModeLocal) {
			AddressingLength.R32 -> this.getOperands32(processor, rm, rmD)
			AddressingLength.R16 -> this.getOperands16(processor, rm, rmD)
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}

	fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String
	fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String
	fun handle16(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>)
	fun handle32(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>)
}