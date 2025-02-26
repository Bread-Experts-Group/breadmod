package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction

interface ZeroOperandOperatingLengthDependentInstruction : ZeroOperandInstruction {
	override fun handle(processor: IA32Processor) {
		when (processor.operatingModeLocal) {
			AddressingLength.R32 -> this.handle32(processor)
			AddressingLength.R16 -> this.handle16(processor)
			else                 -> throw IllegalArgumentException("Cannot support ${processor.operatingModeLocal}")
		}
	}

	override fun getOperands(processor: IA32Processor): String =
		when (processor.operatingModeLocal) {
			AddressingLength.R32 -> this.getOperands32(processor)
			AddressingLength.R16 -> this.getOperands16(processor)
			else                 -> throw IllegalArgumentException("Cannot support ${processor.operatingModeLocal}")
		}

	fun getOperands32(processor: IA32Processor): String
	fun handle32(processor: IA32Processor)
	fun getOperands16(processor: IA32Processor): String
	fun handle16(processor: IA32Processor)
}