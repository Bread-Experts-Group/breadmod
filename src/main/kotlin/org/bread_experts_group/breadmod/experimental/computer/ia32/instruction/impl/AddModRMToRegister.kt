package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32Instruction(0x03u)
object AddModRMToRegister : Instruction("add"), ModRM, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val (memRM, register) = processor.rm()
			val result = this.setFlagsForOperationR(processor, register.get().toUInt(), memRM.getRMi())
			register.set(result.toULong())
			this.setFlagsForResult(processor, result)
		}
		AddressingLength.R16 -> {
			val (memRM, register) = processor.rm()
			val result = this.setFlagsForOperationR(processor, register.get().toUShort(), memRM.getRMs())
			register.set(result.toULong())
			this.setFlagsForResult(processor, result)
		}
		else                 -> throw UnsupportedOperationException()
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}