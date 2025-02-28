package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hD1

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

object ShiftModRMRightOnce : RegisterMemorySingleOperandInstruction, LogicalArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "shr"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, 1"

	override fun handle(processor: IA32Processor, rmM: DecodingUtil.MemRMResult, rmR: KMutableProperty0<ULong>) {
		val a = rmM.getValue()
		val result = a shr 1
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
		processor.flags.setFlag(FlagType.CARRY_FLAG, a.takeLowestOneBit() > 0u)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}