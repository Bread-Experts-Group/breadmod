package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32Instruction(0xF3A6u)
object CompareBytes : ZeroOperandOperatingLengthDependentInstruction, ArithmeticSubtractionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "repe cmps"
	override fun getOperands16(processor: IA32Processor): String =
		"ds:[si], es:[di [${hex(processor.ds.offset(processor.si.x))}] + cx [${hex(processor.c.tx)}]]"

	override fun getOperands32(processor: IA32Processor): String =
		"ds:[esi], es:[edi [${hex(processor.ds.offset(processor.si.x))}] + ecx [${hex(processor.c.tex)}]]"

	override fun handle16(processor: IA32Processor) {
		while (processor.c.x > 0u) {
			processor.c.x--
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt(processor.es.offset(processor.di.x)).toULong(),
				processor.computer.requestMemoryAt(processor.ds.offset(processor.si.x))
			)
			processor.si.x = processor.si.x + 1u
			processor.di.x = processor.di.x + 1u
			this.setFlagsForResult(processor, result)
			if (!processor.flags.getFlag(FlagType.ZERO_FLAG)) break
		}
	}

	override fun handle32(processor: IA32Processor) {
		TODO("CMP 32")
	}
}