package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HE8InstructionCALL {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		val relative = processor.decoding.readBinaryI(2).toShort()
		processor.logger.warn("CALL NEAR/REL $relative")
		processor.push16(processor.ip.tx)
		processor.ip.ex = (processor.ip.tx.toInt() + relative).toUShort().toULong()
	}
}