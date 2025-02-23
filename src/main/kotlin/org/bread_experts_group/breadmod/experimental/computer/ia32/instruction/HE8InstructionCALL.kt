package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HE8InstructionCALL : Instruction {
	override fun getOperands32(processor: IA32Processor): String =
		hex(processor.ip.tex.toInt() + processor.decoding.readBinaryI(4).toInt()) +
				" [${hex(processor.ip.tex)}]"

	override fun handle32(processor: IA32Processor) {
		var relative = processor.decoding.readBinaryI(4).toInt()
		processor.push32(processor.ip.tex)
		processor.ip.tex = (processor.ip.tex.toInt() + relative).toUInt()
	}

	override fun getOperands16(processor: IA32Processor): String =
		hex((processor.ip.tx.toShort() + processor.decoding.readBinaryI(2)).toShort()) +
				" [${hex(processor.ip.tx)}]"

	override fun handle16(processor: IA32Processor) {
		var relative = processor.decoding.readBinaryI(2).toShort()
		processor.push16(processor.ip.tx)
		processor.ip.ex = (processor.ip.tx.toShort() + relative).toUShort().toULong()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}