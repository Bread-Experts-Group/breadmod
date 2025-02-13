package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HEBInstructionJMP {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		val relative = processor.fetch().let { processor.cir.toByte() }
		processor.logger.warn("JMP SHORT $relative")
		processor.ip.tex = (processor.ip.tex.toInt() + relative).toUInt()
	}
}