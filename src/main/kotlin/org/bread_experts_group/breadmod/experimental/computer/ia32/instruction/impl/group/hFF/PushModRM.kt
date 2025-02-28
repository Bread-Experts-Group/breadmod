package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import kotlin.reflect.KMutableProperty0

object PushModRM : RegisterMemoryOperatingLengthDependentSingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "push"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		rmD.memRM

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		rmD.memRM

	override fun handle16(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>): Unit =
		processor.push16(rmM.getValue().toUShort())

	override fun handle32(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>): Unit =
		processor.push32(rmM.getValue().toUInt())

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}