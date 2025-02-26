package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemory8SingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import kotlin.reflect.KMutableProperty0

@IA32InstructionCluster
class MoveFromRegisterDefinitions(processor: IA32Processor) {
	class MoveRegisterToModRM(type: RegisterType) : RegisterMemorySingleOperandInstruction {
		override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = "mov"
		override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
			"${rmD.memRM}, ${rmD.register}"

		override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
			rmM.setValue(rmR.get())
		}

		override val rmRegisterType: RegisterType = type
	}

	@IA32Instruction(0x89u)
	val rm: MoveRegisterToModRM = MoveRegisterToModRM(RegisterType.GENERAL_PURPOSE)

	@IA32Instruction(0x8Cu)
	val seg: MoveRegisterToModRM = MoveRegisterToModRM(RegisterType.SEGMENT)

	class MoveRegister8ToModRM(type: RegisterType) : RegisterMemory8SingleOperandInstruction {
		override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = "mov"
		override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
			"${rmD.memRM}, ${rmD.register}"

		override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
			rmM.setValue(rmR.get())
		}

		override val rmRegisterType: RegisterType = type
	}

	@IA32Instruction(0x88u)
	val rm8: MoveRegister8ToModRM = MoveRegister8ToModRM(RegisterType.GENERAL_PURPOSE)
}