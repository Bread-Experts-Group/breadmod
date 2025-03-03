package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32Instruction(0x39u)
object CompareRegisterToModRM : Instruction("cmp"), ModRM, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }
	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val (memRm, register) = processor.rm()
			val result = this.setFlagsForOperationR(processor, memRm.getRMi(), register.get().toUInt())
			this.setFlagsForResult(processor, result)
		}
		AddressingLength.R16 -> {
			val (memRm, register) = processor.rm()
			val result = this.setFlagsForOperationR(processor, memRm.getRMs(), register.get().toUShort())
			this.setFlagsForResult(processor, result)
		}
		else                 -> throw UnsupportedOperationException()
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}