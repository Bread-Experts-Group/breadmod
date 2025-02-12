package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H59InstructionPOP {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride) TODO("can't support CS")
		if (processor.bitOverride) processor.c.tex = processor.pop32()
		else processor.c.tx = processor.pop16()
	}
}