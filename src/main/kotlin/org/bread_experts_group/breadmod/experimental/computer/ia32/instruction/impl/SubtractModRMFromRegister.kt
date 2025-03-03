package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32Instruction(0x2Bu)
object SubtractModRMFromRegister : Instruction("sub"), ModRM, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		when (processor.operandSize) {
			DecodingUtil.AddressingLength.R32 -> {
				this.setFlagsForOperationR(processor, register.get().toUInt(), memRM.getRMi()).also {
					register.set(it.toULong())
					this.setFlagsForResult(processor, it)
				}
			}
			DecodingUtil.AddressingLength.R16 -> {
				this.setFlagsForOperationR(processor, register.get().toUShort(), memRM.getRMs()).also {
					register.set(it.toULong())
					this.setFlagsForResult(processor, it)
				}
			}
			else                              -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}