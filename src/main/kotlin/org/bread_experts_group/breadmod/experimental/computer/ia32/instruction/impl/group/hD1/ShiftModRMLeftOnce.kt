package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hD1

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

object ShiftModRMLeftOnce : RegisterMemoryOperatingLengthDependentSingleOperandInstruction,
	LogicalArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "shl"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, 1"

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, 1"

	override fun handle16(processor: IA32Processor, rmM: DecodingUtil.MemRMResult, rmR: KMutableProperty0<ULong>) {
		val a = rmM.getValue().toUShort()
		val result = a.toULong() shl 1
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
		processor.flags.setFlag(FlagType.CARRY_FLAG, a.takeHighestOneBit() > 0u)
	}

	override fun handle32(processor: IA32Processor, rmM: DecodingUtil.MemRMResult, rmR: KMutableProperty0<ULong>) {
		val a = rmM.getValue().toUInt()
		val result = a.toULong() shl 1
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
		processor.flags.setFlag(FlagType.CARRY_FLAG, a.takeHighestOneBit() > 0u)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}