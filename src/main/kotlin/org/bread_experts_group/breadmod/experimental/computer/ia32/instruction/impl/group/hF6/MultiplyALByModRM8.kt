package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF6

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemory8SingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

object MultiplyALByModRM8 : RegisterMemory8SingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "mul"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = rmD.memRM
	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		processor.a.x = processor.a.l * rmM.getValue()
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, processor.a.h > 0u)
		processor.flags.setFlag(FlagType.CARRY_FLAG, processor.a.h > 0u)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}