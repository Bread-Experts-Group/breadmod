package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object NearJumpToModRM : Instruction("jmp"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().regMem
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		processor.ip.tex = when (processor.operandSize) {
			AddressingLength.R32 -> memRM.getRMi()
			AddressingLength.R16 -> memRM.getRMs().toUInt()
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}