package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32Instruction(0x13u)
object AddWithCarryModRMToRegister : Instruction("adc"), ModRM, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	private fun carryStat(processor: IA32Processor): UByte =
		if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u

	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = this.setFlagsForOperationR(
					processor,
					register.get().toUInt(),
					memRM.getRMi() + this.carryStat(processor)
				)
				register.set(result.toULong())
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = this.setFlagsForOperationR(
					processor,
					register.get().toUShort(),
					(memRM.getRMs() + this.carryStat(processor)).toUShort()
				)
				register.set(result.toULong())
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}