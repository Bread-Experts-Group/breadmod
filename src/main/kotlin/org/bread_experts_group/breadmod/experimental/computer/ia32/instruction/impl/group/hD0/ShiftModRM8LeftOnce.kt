package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hD0

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.shl
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object ShiftModRM8LeftOnce : Instruction("shl"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD(
		AddressingLength.R8
	).let { "${it.regMem}, 1" }

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm(AddressingLength.R8)
		val a = memRM.getRMb()
		val result = a shl 1
		memRM.setRMb(result)
		this.setFlagsForResult(processor, result)
		processor.flags.setFlag(FlagType.CARRY_FLAG, a.takeHighestOneBit() > 0u)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}