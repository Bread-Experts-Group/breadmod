package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister

object H75InstructionJNZ : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		hex((processor.ip.tex.toInt() + processor.decoding.readBinaryI(1).toByte()))

	override fun handle16(processor: IA32Processor) {
		val relative = processor.decoding.readBinaryI(1).toByte()
		if (!processor.flags.getFlag(FlagsRegister.FlagType.ZERO_FLAG)) {
			processor.ip.ex = (processor.ip.ex.toInt() + relative).toULong()
		}
	}

	override fun getOperands32(processor: IA32Processor): String = H74InstructionJE.getOperands16(processor)
	override fun handle32(processor: IA32Processor) = H74InstructionJE.handle16(processor)

	override val supportsCodeSegmentOverride: Boolean = false
}