package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32InstructionCluster
class MoveFromRegisterDefinitions(processor: IA32Processor) {
	class MoveRegisterToModRM(type: RegisterType) : Instruction("mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }
		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm()
			when (processor.operandSize) {
				AddressingLength.R32 -> memRM.setRMi(register.get().toUInt())
				AddressingLength.R16 -> memRM.setRMs(register.get().toUShort())
				else                 -> throw UnsupportedOperationException()
			}
		}

		override val registerType: RegisterType = type
	}

	@IA32Instruction(0x89u)
	val rm: MoveRegisterToModRM = MoveRegisterToModRM(RegisterType.GENERAL_PURPOSE)

	@IA32Instruction(0x8Cu)
	val seg: MoveRegisterToModRM = MoveRegisterToModRM(RegisterType.SEGMENT)

	class MoveRegister8ToModRM(type: RegisterType) : Instruction("mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
			"${it.regMem}, ${it.register}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm(AddressingLength.R8)
			memRM.setRMb(register.get().toUByte())
		}

		override val registerType: RegisterType = type
	}

	@IA32Instruction(0x88u)
	val rm8: MoveRegister8ToModRM = MoveRegister8ToModRM(RegisterType.GENERAL_PURPOSE)
}