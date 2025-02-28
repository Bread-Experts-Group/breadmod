package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength

interface Immediate8SingleOperandOperatingLengthDependentInstruction : Immediate8SingleOperandInstruction {
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = when (processor.operatingModeLocal) {
		AddressingLength.R32 -> this.getOperands32(processor, imm8)
		AddressingLength.R16 -> this.getOperands16(processor, imm8)
		else                 -> throw IllegalArgumentException("Unsupported mode")
	}

	override fun handle(processor: IA32Processor, imm8: UByte): Unit = when (processor.operatingModeLocal) {
		AddressingLength.R32 -> this.handle32(processor, imm8)
		AddressingLength.R16 -> this.handle16(processor, imm8)
		else                 -> throw IllegalArgumentException("Unsupported mode")
	}

	fun getOperands32(processor: IA32Processor, imm8: UByte): String
	fun getOperands16(processor: IA32Processor, imm8: UByte): String
	fun handle32(processor: IA32Processor, imm8: UByte)
	fun handle16(processor: IA32Processor, imm8: UByte)
}