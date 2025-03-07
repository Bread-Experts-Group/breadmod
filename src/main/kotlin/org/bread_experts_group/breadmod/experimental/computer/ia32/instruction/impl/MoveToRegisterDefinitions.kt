package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32InstructionCluster
class MoveToRegisterDefinitions(processor: IA32Processor) {
	class MoveModRMToRegister(type: RegisterType) : Instruction("mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD().let {
			"${it.register}, ${it.regMem}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm()
			when (processor.operandSize) {
				AddressingLength.R32 -> register.set(memRM.getRMi().toULong())
				AddressingLength.R16 -> register.set(memRM.getRMs().toULong())
				else                 -> throw UnsupportedOperationException()
			}
		}

		override val registerType: RegisterType = type
	}

	@IA32Instruction(0x8Bu)
	val rm: MoveModRMToRegister = MoveModRMToRegister(RegisterType.GENERAL_PURPOSE)

	@IA32Instruction(0x8Eu)
	val seg: MoveModRMToRegister = MoveModRMToRegister(RegisterType.SEGMENT)

	class MoveModRMToRegister8(type: RegisterType) : Instruction("mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
			"${it.register}, ${it.regMem}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm(AddressingLength.R8)
			register.set(memRM.getRMb().toULong())
		}

		override val registerType: RegisterType = type
	}

	@IA32Instruction(0x8Au)
	val rm8: MoveModRMToRegister8 = MoveModRMToRegister8(RegisterType.GENERAL_PURPOSE)
}