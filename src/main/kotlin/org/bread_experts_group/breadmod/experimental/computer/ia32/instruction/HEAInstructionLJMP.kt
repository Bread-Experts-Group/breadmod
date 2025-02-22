package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HEAInstructionLJMP : Instruction {
	override fun handle16(processor: IA32Processor) {
		val tempIP = processor.decoding.readBinaryI(2).toUInt()
		val tempCS = processor.decoding.readBinaryI(2).toUShort()
		processor.ip.tex = tempIP
		processor.cs.tx = tempCS
		processor.logger.warn("JMP FAR ${processor.cs.hex(processor.ip)}")
	}

	override val supportsCodeSegmentOverride: Boolean = false
}