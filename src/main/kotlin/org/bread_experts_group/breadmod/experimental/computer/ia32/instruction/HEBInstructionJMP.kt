package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HEBInstructionJMP : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		hex((processor.ip.tx.toShort() + processor.decoding.readBinaryI(1).toByte()).toShort())

	override fun handle16(processor: IA32Processor) {
		val relative = processor.decoding.readBinaryI(1).toByte()
		processor.ip.tex = (processor.ip.tex.toInt() + relative).toUInt()
		if (relative.toInt() == -2) throw IllegalStateException("Infinite loop")
	}

	override val supportsCodeSegmentOverride: Boolean = false
}