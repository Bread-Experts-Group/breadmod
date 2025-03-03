package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF6

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.and
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object ModRM8ImmediateTEST : Instruction("test"), ModRM, Immediate8, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = "${processor.rmD().regMem}, ${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = memRM.getRMi() and processor.imm8()
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = memRM.getRMs() and processor.imm8()
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: DecodingUtil.RegisterType = DecodingUtil.RegisterType.GENERAL_PURPOSE
}