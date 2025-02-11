package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H46InstructionINC {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		if (processor.bitOverride) processor.si.ex++
		else processor.si.x++
	}
}