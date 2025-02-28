package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction

@IA32Instruction(0xAAu)
object StoreByte : ZeroOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "stosb"
	override fun getOperands16(processor: IA32Processor): String =
		"es:di [${hex(processor.es.offset(processor.di.x).toUShort())}], al"

	override fun getOperands32(processor: IA32Processor): String =
		"es:edi [${hex(processor.es.offset(processor.di.ex).toUInt())}], al"

	override fun handle16(processor: IA32Processor) {
		processor.computer.setMemoryAt(
			processor.es.offset(processor.di.x),
			processor.a.tl
		)
		processor.di.x++
	}

	override fun handle32(processor: IA32Processor) {
		processor.computer.setMemoryAt(
			processor.es.offset(processor.di.ex),
			processor.a.tl
		)
		processor.di.ex++
	}
}