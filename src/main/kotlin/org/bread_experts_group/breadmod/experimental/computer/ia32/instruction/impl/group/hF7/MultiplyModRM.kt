package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

object MultiplyModRM : RegisterMemoryOperatingLengthDependentSingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "mul"
	override fun getOperands16(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"dx:ax, ax [${hex(processor.a.tx)}] * ${rmD.memRM}"

	override fun getOperands32(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"edx:eax, eax [${hex(processor.a.tex)}] * ${rmD.memRM}"

	override fun handle16(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = processor.a.x * rmM.getValue()
		processor.d.x = result shr 16
		processor.a.x = result
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, processor.d.x > 0u)
		processor.flags.setFlag(FlagType.CARRY_FLAG, processor.d.x > 0u)
	}

	override fun handle32(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = processor.a.ex * rmM.getValue()
		processor.d.ex = result shr 32
		processor.a.ex = result
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, processor.d.ex > 0u)
		processor.flags.setFlag(FlagType.CARRY_FLAG, processor.d.ex > 0u)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}