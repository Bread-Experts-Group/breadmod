package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import kotlin.reflect.KMutableProperty0

object DivideModRM : RegisterMemoryOperatingLengthDependentSingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "div"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"dx:ax, ${rmD.memRM}"

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"edx:eax, ${rmD.memRM}"

	override fun handle16(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val dividend = ((processor.d.x shl 16) or processor.a.x)
		processor.a.x = dividend / rmM.getValue()
		processor.d.x = dividend % rmM.getValue()
	}

	override fun handle32(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val dividend = ((processor.d.ex shl 32) or processor.a.ex)
		processor.a.ex = dividend / rmM.getValue()
		processor.d.ex = dividend % rmM.getValue()
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}