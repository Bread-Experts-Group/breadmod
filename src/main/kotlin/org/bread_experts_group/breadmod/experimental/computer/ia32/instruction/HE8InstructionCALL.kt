package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HE8InstructionCALL : Instruction {
	override fun handle32(processor: IA32Processor) {
		var relative = processor.decoding.readBinaryI(4).toInt()
		processor.logger.warn("CALL32 NEAR/REL $relative")
		processor.push32(processor.ip.tex)
		processor.ip.tex = (processor.ip.tex.toInt() + relative).toUInt()
	}

	override fun handle16(processor: IA32Processor) {
		var relative = processor.decoding.readBinaryI(2).toShort()
		processor.logger.warn("CALL16 NEAR/REL $relative")
		processor.push16(processor.ip.tx)
		processor.ip.ex = (processor.ip.tx.toInt() + relative).toUShort().toULong()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}