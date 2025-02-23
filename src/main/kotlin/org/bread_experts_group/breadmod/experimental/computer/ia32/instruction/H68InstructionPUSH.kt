package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H68InstructionPUSH : Instruction {
	override fun getOperands16(processor: IA32Processor): String = hex(processor.decoding.readBinaryI(2).toUShort())

	override fun handle16(processor: IA32Processor) {
		processor.push16(processor.decoding.readBinaryI(2).toUShort())
	}

	override val supportsCodeSegmentOverride: Boolean = false
}