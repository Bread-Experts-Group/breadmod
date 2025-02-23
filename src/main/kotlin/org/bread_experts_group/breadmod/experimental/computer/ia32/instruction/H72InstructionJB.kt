package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister

object H72InstructionJB : Instruction {
	override fun getOperands16(processor: IA32Processor): String =
		hex((processor.ip.tx.toShort() + processor.decoding.readBinaryI(1).toByte()).toShort())

	override fun handle16(processor: IA32Processor) {
		val relative = processor.decoding.readBinaryI(1).toByte()
		if (processor.flags.getFlag(FlagsRegister.FlagType.CARRY_FLAG))
			processor.ip.ex = (processor.ip.ex.toInt() + relative).toULong()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}