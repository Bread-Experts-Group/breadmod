package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HEAInstructionLJMP {
	fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		val tempIP = processor.decoding.readBinaryI(2).toUInt()
		val tempCS = processor.decoding.readBinaryI(2).toUShort()
		processor.ip.tex = tempIP
		processor.cs.tx = tempCS
		processor.logger.warn("JMP FAR ${processor.cs.hex(processor.ip)}")
	}
}