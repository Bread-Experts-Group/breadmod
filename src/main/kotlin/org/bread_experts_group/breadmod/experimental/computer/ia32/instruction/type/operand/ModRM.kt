package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType

interface ModRM {
	fun IA32Processor.rm(operandLength: AddressingLength = this.operandSize): ModRMResult = this.decoding.getModRM(
		this.decoding.readFetch(),
		this@ModRM.registerType,
		operandLength
	)

	fun IA32Processor.rmD(operandLength: AddressingLength = this.operandSize): ModRMDisassemblyResult =
		this.decoding.getModRMDisassembler(
			this.decoding.readFetch(),
			this@ModRM.registerType,
			operandLength
		)

	fun IA32Processor.rmB(operandLength: AddressingLength = this.operandSize): Pair<ModRMResult, ModRMDisassemblyResult> {
		val saved = this.ip.rx
		val o1 = this.rm(operandLength)
		this.ip.rx = saved
		return o1 to this.rmD(operandLength)
	}

	val registerType: RegisterType
}