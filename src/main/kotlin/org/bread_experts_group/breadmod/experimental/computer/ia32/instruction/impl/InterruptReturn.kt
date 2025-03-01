package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction

@IA32Instruction(0xCFu)
object InterruptReturn : ZeroOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "iret"
	override fun getOperands16(processor: IA32Processor): String = ""
	override fun getOperands32(processor: IA32Processor): String = ""
	override fun handle16(processor: IA32Processor) {
		processor.ip.tex = processor.pop16().toUInt()
		processor.cs.tx = processor.pop16()
		processor.flags.tx = processor.pop16()
	}

	override fun handle32(processor: IA32Processor) {
		TODO("Not yet implemented")
	}
}