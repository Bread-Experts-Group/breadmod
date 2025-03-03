package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object TestModRM : Instruction("test"), ModRM, LogicalArithmeticFlagOperations, Immediate32, Immediate16 {
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
				val result = memRM.getRMi() and processor.imm32()
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = memRM.getRMs() and processor.imm16()
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}