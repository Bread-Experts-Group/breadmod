package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HA3InstructionMOV : Instruction {
	override fun getOperands32(processor: IA32Processor): String {
		val offset = processor.ds.offset(processor.decoding.readBinaryI(4).toULong())
		return "[${hex(offset.toUInt())}], eax [${hex(processor.a.tex)}]"
	}

	override fun handle32(processor: IA32Processor) {
		val offset = processor.ds.offset(processor.decoding.readBinaryI(4).toULong())
		processor.computer.setMemoryAt32(offset, processor.a.tex)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}