package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hFE

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemory8SingleOperandInstruction
import kotlin.reflect.KMutableProperty0

object DecrementModRM8 : RegisterMemory8SingleOperandInstruction, ArithmeticSubtractionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "dec"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = rmD.memRM
	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), (1u).toUByte())
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}