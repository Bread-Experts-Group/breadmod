package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength

interface ImmediateSigned8SingleOperandOperatingLengthDependentInstruction : ImmediateSigned8SingleOperandInstruction {
	fun getOperands16(processor: IA32Processor, rel8: Byte): String
	fun getOperands32(processor: IA32Processor, rel8: Byte): String
	fun handle16(processor: IA32Processor, rel8: Byte)
	fun handle32(processor: IA32Processor, rel8: Byte)

	override fun getOperands(processor: IA32Processor, rel8: Byte): String = when (processor.operatingModeLocal) {
		AddressingLength.R32 -> this.getOperands16(processor, rel8)
		AddressingLength.R16 -> this.getOperands32(processor, rel8)
		else                 -> throw IllegalArgumentException("Mode not supported")
	}

	override fun handle(processor: IA32Processor, rel8: Byte): Unit = when (processor.operatingModeLocal) {
		AddressingLength.R32 -> this.handle16(processor, rel8)
		AddressingLength.R16 -> this.handle32(processor, rel8)
		else                 -> throw IllegalArgumentException("Mode not supported")
	}
}