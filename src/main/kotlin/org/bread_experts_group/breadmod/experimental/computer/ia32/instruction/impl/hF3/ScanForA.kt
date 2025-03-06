package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32Instruction(0xF2AFu)
object ScanForA : Instruction("repne scas"), ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "eax [${hex(processor.a.tex)}], es:[edi [${hex(processor.di.tex)}]]"
		AddressingLength.R16 -> "ax [${hex(processor.a.tx)}], es:[di [${hex(processor.di.tx)}]]"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> while (processor.c.ex > 0u) {
			processor.c.ex--
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt32(processor.es.offset(processor.di)),
				processor.a.tex
			)
			this.setFlagsForResult(processor, result)
			processor.di.ex += 4u
			if (processor.flags.getFlag(FlagType.ZERO_FLAG)) break
		}
		AddressingLength.R16 -> while (processor.c.x > 0u) {
			processor.c.x--
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt16(processor.es.offset(processor.di)),
				processor.a.tx
			)
			this.setFlagsForResult(processor, result)
			processor.di.x += 2u
			if (processor.flags.getFlag(FlagType.ZERO_FLAG)) break
		}
		else                 -> throw UnsupportedOperationException()
	}
}