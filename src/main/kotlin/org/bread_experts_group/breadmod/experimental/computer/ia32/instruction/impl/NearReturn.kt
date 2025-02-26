package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction

@IA32Instruction(0xC3u)
object NearReturn : ZeroOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "ret"
	override fun getOperands16(processor: IA32Processor): String = hex(processor.pop16())
	override fun getOperands32(processor: IA32Processor): String = hex(processor.pop32())
	override fun handle16(processor: IA32Processor) {
		processor.ip.tex = processor.pop16().toUInt()
	}

	override fun handle32(processor: IA32Processor) {
		processor.ip.tex = processor.pop32()
	}
}