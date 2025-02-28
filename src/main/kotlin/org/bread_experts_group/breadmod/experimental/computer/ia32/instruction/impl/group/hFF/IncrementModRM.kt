package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import kotlin.reflect.KMutableProperty0

object IncrementModRM : RegisterMemoryOperatingLengthDependentSingleOperandInstruction,
	ArithmeticAdditionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "inc"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		rmD.memRM

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		rmD.memRM

	override fun handle16(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), (1u).toUShort())
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
	}

	override fun handle32(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), (1u).toUInt())
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}