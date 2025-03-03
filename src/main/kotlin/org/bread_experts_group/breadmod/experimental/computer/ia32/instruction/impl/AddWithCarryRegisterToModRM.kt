package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.toUInt
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.toUShort
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32Instruction(0x11u)
object AddWithCarryRegisterToModRM : Instruction("adc"), ModRM, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val (memRM, register) = processor.rm()
			val result = this.setFlagsForOperationR(
				processor,
				memRM.getRMi(),
				(register.get() + processor.flags.getFlag(FlagType.CARRY_FLAG).toUInt()).toUInt()
			)
			memRM.setRMi(result)
			this.setFlagsForResult(processor, result)
		}
		AddressingLength.R16 -> {
			val (memRM, register) = processor.rm()
			val result = this.setFlagsForOperationR(
				processor,
				memRM.getRMs(),
				(register.get() + processor.flags.getFlag(FlagType.CARRY_FLAG).toUShort()).toUShort()
			)
			memRM.setRMs(result)
			this.setFlagsForResult(processor, result)
		}
		else                 -> throw UnsupportedOperationException()
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}