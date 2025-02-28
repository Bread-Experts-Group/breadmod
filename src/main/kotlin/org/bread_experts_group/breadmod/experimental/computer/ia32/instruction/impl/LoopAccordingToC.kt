package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateSigned8SingleOperandOperatingLengthDependentInstruction

@IA32Instruction(0xE2u)
object LoopAccordingToC : ImmediateSigned8SingleOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "loop"
	private fun getOperandsPrefix(processor: IA32Processor, rel8: Byte): String =
		"${hex(rel8)} [${hex((processor.ip.tex.toInt() + rel8).toUInt())}]"

	override fun getOperands16(processor: IA32Processor, rel8: Byte): String =
		"${this.getOperandsPrefix(processor, rel8)} [cx ${hex(processor.c.tx)}]"

	override fun getOperands32(processor: IA32Processor, rel8: Byte): String =
		"${this.getOperandsPrefix(processor, rel8)} [ecx ${hex(processor.c.tex)}]"

	override fun handle16(processor: IA32Processor, rel8: Byte) {
		processor.c.x--
		if (processor.c.x > 0u) processor.ip.tex = (processor.ip.tex.toInt() + rel8).toUInt()
	}

	override fun handle32(processor: IA32Processor, rel8: Byte) {
		processor.c.ex--
		if (processor.c.ex > 0u) processor.ip.tex = (processor.ip.tex.toInt() + rel8).toUInt()
	}
}