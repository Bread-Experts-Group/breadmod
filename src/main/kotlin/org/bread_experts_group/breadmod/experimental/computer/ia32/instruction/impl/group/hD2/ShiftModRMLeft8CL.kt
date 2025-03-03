package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hD2

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.shl
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object ShiftModRMLeft8CL : Instruction("shl"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
		"${it.regMem}, cl [${hex(processor.c.tl)}]"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm(AddressingLength.R8)
		val result = memRM.getRMb() shl processor.c.tl.toInt()
		memRM.setRMb(result)
		this.setFlagsForResult(processor, result)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}