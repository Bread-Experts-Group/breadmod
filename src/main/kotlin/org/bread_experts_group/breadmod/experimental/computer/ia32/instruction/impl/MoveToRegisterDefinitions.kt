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
class MoveToRegisterDefinitions(processor: IA32Processor) {
	class MoveModRMToRegister(type: RegisterType) : RegisterMemorySingleOperandInstruction {
		override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = "mov"
		override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
			"${rmD.register}, ${rmD.memRM}"

		override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
			rmR.set(rmM.getValue())
		}

		override val rmRegisterType: RegisterType = type
	}

	@IA32Instruction(0x8Bu)
	val rm: MoveModRMToRegister = MoveModRMToRegister(RegisterType.GENERAL_PURPOSE)

	@IA32Instruction(0x8Eu)
	val seg: MoveModRMToRegister = MoveModRMToRegister(RegisterType.SEGMENT)

	class MoveModRMToRegister8(type: RegisterType) : RegisterMemory8SingleOperandInstruction {
		override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = "mov"
		override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
			"${rmD.register}, ${rmD.memRM}"

		override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
			rmR.set(rmM.getValue())
		}

		override val rmRegisterType: RegisterType = type
	}

	@IA32Instruction(0x8Au)
	val rm8: MoveModRMToRegister8 = MoveModRMToRegister8(RegisterType.GENERAL_PURPOSE)
}