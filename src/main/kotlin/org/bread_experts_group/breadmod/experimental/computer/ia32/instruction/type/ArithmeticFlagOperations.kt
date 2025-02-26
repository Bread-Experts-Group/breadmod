package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

interface ArithmeticFlagOperations {
	fun setFlagsForResult(processor: IA32Processor, v: UByte): Unit = this.setFlagsForResult(processor, v.toULong())
	fun setFlagsForResult(processor: IA32Processor, v: UShort): Unit = this.setFlagsForResult(processor, v.toULong())
	fun setFlagsForResult(processor: IA32Processor, v: UInt): Unit = this.setFlagsForResult(processor, v.toULong())
	fun setFlagsForResult(processor: IA32Processor, v: ULong) {
		processor.flags.setFlagToResult(FlagType.SIGN_FLAG, v)
		processor.flags.setFlagToResult(FlagType.ZERO_FLAG, v)
		processor.flags.setFlagToResult(FlagType.PARITY_FLAG, v)
	}
}