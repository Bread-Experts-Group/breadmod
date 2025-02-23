package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HEAInstructionLJMP : Instruction {
	override fun getOperands16(processor: IA32Processor): String {
		val tempIP = processor.decoding.readBinaryI(2).toUShort()
		val tempCS = processor.decoding.readBinaryI(2).toUShort()
		return "${hex(tempCS)}:${hex(tempIP).substring(2)}"
	}

	override fun handle16(processor: IA32Processor) {
		val tempIP = processor.decoding.readBinaryI(2).toUInt()
		val tempCS = processor.decoding.readBinaryI(2).toUShort()
		processor.ip.tex = tempIP
		processor.cs.tx = tempCS
	}

	override fun getOperands32(processor: IA32Processor): String {
		val tempIP = processor.decoding.readBinaryI(4).toUInt()
		val tempCS = processor.decoding.readBinaryI(2).toUShort()
		return "${hex(tempCS)}:${hex(tempIP).substring(2)}"
	}

	override fun handle32(processor: IA32Processor) {
		val tempIP = processor.decoding.readBinaryI(4).toUInt()
		val tempCS = processor.decoding.readBinaryI(2).toUShort()
		processor.ip.tex = tempIP
		processor.cs.tx = tempCS
	}

	override val supportsCodeSegmentOverride: Boolean = false
}