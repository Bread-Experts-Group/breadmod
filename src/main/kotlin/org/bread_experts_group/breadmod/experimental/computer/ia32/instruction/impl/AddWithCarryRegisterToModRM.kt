package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.BreadMod.Companion.processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

@IA32Instruction(0x11u)
object AddWithCarryRegisterToModRM : RegisterMemoryOperatingLengthDependentSingleOperandInstruction,
	ArithmeticAdditionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "adc"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, ${rmD.register}"

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, ${rmD.register}"

	private fun carryStat() = if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u

	override fun handle16(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), rmR.get().toUShort() + this.carryStat())
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
	}

	override fun handle32(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), rmR.get().toUInt() + this.carryStat())
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}