package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h8F

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import kotlin.reflect.KMutableProperty0

object PopIntoModRM : RegisterMemoryOperatingLengthDependentSingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "pop"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, ${hex(processor.pop16())}"

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, ${hex(processor.pop32())}"

	override fun handle16(processor: IA32Processor, rmM: DecodingUtil.MemRMResult, rmR: KMutableProperty0<ULong>) {
		rmM.setValue(processor.pop16().toULong())
	}

	override fun handle32(processor: IA32Processor, rmM: DecodingUtil.MemRMResult, rmR: KMutableProperty0<ULong>) {
		rmM.setValue(processor.pop32().toULong())
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}