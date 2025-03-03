package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h80

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object SubtractImmediateFromModRM8 : Instruction("sub"), ModRM, Immediate8, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String =
		processor.rmD(AddressingLength.R8).let { "${it.regMem}, ${hex(processor.imm8())}" }

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm(AddressingLength.R8)
		val result = this.setFlagsForOperationR(processor, memRM.getRMb(), processor.imm8())
		memRM.setRMb(result)
		this.setFlagsForResult(processor, result)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}