package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H50InstructionPUSH {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		if (processor.bitOverride) processor.push32(processor.a.t_ex)
		else processor.push16(processor.a.t_x)
	}
}