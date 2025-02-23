package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object HFBInstructionSTI : Instruction {
	fun handle(processor: IA32Processor) {
		processor.flags.setFlag(FlagType.INTERRUPT_ENABLE_FLAG, true)
	}

	override fun getOperands16(processor: IA32Processor): String = ""
	override fun getOperands32(processor: IA32Processor): String = ""
	override fun getOperands64(processor: IA32Processor): String = ""
	override fun handle16(processor: IA32Processor) = handle(processor)
	override fun handle32(processor: IA32Processor) = handle(processor)
	override fun handle64(processor: IA32Processor) = handle(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}