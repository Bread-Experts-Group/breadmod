package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h8F

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object PopIntoModRM : Instruction("pop"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		when (processor.operandSize) {
			AddressingLength.R32 -> "${it.regMem}, ${hex(processor.pop32())}"
			AddressingLength.R16 -> "${it.regMem}, ${hex(processor.pop16())}"
			else                 -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> memRM.setRMi(processor.pop32())
			AddressingLength.R16 -> memRM.setRMs(processor.pop16())
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}