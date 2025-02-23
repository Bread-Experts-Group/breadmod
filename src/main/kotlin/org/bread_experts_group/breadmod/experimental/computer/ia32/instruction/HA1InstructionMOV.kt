package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HA1InstructionMOV : Instruction {
	override fun getOperands32(processor: IA32Processor): String {
		val offset = processor.ds.offset(processor.decoding.readBinaryI(4).toULong())
		return "eax, [${hex(offset.toUInt())} [${hex(processor.computer.requestMemoryAt32(offset))}]]"
	}

	override fun handle32(processor: IA32Processor) {
		val offset = processor.ds.offset(processor.decoding.readBinaryI(4).toULong())
		processor.a.tex = processor.computer.requestMemoryAt32(offset)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}