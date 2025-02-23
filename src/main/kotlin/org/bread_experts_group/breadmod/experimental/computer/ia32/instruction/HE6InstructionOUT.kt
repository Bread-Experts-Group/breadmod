package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HE6InstructionOUT : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		"${hex(processor.decoding.readBinaryI(1).toUByte())}, al [${hex(processor.a.tl)}]"

	override fun handle16(processor: IA32Processor) {
		processor.decoding.readBinaryI(1).toUByte()
		// TODO???? i/o pins
	}

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)
	override fun handle32(processor: IA32Processor) = handle16(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}