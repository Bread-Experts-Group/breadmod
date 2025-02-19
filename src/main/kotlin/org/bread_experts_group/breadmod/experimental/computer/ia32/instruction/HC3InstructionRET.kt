package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HC3InstructionRET : Instruction {
	override fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		val reset = processor.pop16()
		processor.logger.warn("RET NEAR $reset")
		processor.ip.tex = reset.toUInt()
	}
}