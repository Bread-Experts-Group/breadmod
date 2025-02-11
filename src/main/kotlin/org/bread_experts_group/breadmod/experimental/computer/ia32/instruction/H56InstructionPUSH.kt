package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H56InstructionPUSH {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		if (processor.bitOverride) processor.push32(processor.si.t_ex)
		else processor.push16(processor.si.t_x)
	}
}