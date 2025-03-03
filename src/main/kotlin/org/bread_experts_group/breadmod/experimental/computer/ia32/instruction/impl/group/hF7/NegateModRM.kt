package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object NegateModRM : Instruction("neg"), ModRM, ArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().regMem
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val value = memRM.getRMi()
				memRM.setRMi(0u - value)
				this.setFlagsForResult(processor, memRM.getRMi())
				processor.flags.setFlag(FlagType.CARRY_FLAG, value != UInt.MIN_VALUE)
			}
			AddressingLength.R16 -> {
				val value = memRM.getRMs()
				memRM.setRMi(0u - value)
				this.setFlagsForResult(processor, memRM.getRMs())
				processor.flags.setFlag(FlagType.CARRY_FLAG, value != UShort.MIN_VALUE)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}