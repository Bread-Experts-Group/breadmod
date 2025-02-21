package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor.FLAGSFlagType

object H76InstructionJBE : Instruction {
	override fun handle16(processor: IA32Processor) {
		val relative = processor.fetch().let { processor.cir.toByte() }
		if (processor.getFlag(FLAGSFlagType.CARRY_FLAG) || processor.getFlag(FLAGSFlagType.ZERO_FLAG)) {
			processor.logger.warn("JBE SHORT $relative")
			processor.ip.ex = (processor.ip.ex.toInt() + relative).toULong()
		}
	}

	override val supportsCodeSegmentOverride: Boolean = false
}