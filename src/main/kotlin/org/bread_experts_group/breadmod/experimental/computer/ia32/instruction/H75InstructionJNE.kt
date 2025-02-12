package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor.FlagType

object H75InstructionJNE {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		val relative = processor.fetch().let { processor.cir.toByte() }
		if (!processor.getFlag(FlagType.ZERO_FLAG)) {
			processor.logger.warn("JNE SHORT $relative")
			processor.ip.ex = (processor.ip.ex.toInt() + relative).toULong()
		}
	}
}