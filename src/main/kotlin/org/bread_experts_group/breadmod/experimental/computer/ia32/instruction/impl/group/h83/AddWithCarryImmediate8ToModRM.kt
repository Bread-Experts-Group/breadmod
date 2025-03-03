package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h83

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object AddWithCarryImmediate8ToModRM : Instruction("adc"), ModRM, Immediate8, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = "${processor.rmD().regMem}, ${hex(processor.imm8())}"
	private fun carryStat(processor: IA32Processor): UByte =
		if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		val add = (processor.imm8() + this.carryStat(processor)).toUByte()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMi(), add.toUInt())
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMs(), add.toUShort())
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}