package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object IncrementModRM : Instruction("inc"), ModRM, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().regMem
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMi(), 1u)
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMs(), 1u)
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}