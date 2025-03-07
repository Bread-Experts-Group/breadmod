package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32Instruction(0x87u)
object ExchangeModRMWithRegister : Instruction("xchg"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		val tmp = when (processor.operandSize) {
			AddressingLength.R32 -> memRM.getRMi().toULong()
			AddressingLength.R16 -> memRM.getRMs().toULong()
			else                 -> throw UnsupportedOperationException()
		}
		when (processor.operandSize) {
			AddressingLength.R32 -> memRM.setRMi(register.get().toUInt())
			AddressingLength.R16 -> memRM.setRMs(register.get().toUShort())
			else                 -> throw UnsupportedOperationException()
		}
		register.set(tmp)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}