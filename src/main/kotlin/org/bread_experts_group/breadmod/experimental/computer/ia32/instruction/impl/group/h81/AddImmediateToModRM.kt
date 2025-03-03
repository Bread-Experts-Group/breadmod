package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h81

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object AddImmediateToModRM : Instruction("add"), ModRM, Immediate16, Immediate32, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		when (processor.operandSize) {
			AddressingLength.R32 -> "${it.regMem}, ${hex(processor.imm32())}"
			AddressingLength.R16 -> "${it.regMem}, ${hex(processor.imm16())}"
			else                 -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMi(), processor.imm32())
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMs(), processor.imm16())
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}