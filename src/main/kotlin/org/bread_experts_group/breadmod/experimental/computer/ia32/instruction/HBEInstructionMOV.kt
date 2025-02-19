package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HBEInstructionMOV : Instruction {
	override fun handle(processor: IA32Processor) {
		if (processor.csOverride || processor.bitOverride) TODO("can't support CS/66")
		val imm16 = processor.decoding.readBinaryI(2).toUShort()
		processor.si.tx = imm16
	}
}