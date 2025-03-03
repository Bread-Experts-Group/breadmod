package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister

interface LogicalArithmeticFlagOperations : ArithmeticFlagOperations {
	override fun setFlagsForResult(processor: IA32Processor, v: ULong) {
		processor.flags.setFlag(FlagsRegister.FlagType.OVERFLOW_FLAG, false)
		processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		super.setFlagsForResult(processor, v)
	}
}