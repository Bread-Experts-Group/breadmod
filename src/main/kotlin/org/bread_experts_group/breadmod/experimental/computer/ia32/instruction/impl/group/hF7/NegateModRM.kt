package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

object NegateModRM : RegisterMemorySingleOperandInstruction, ArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "neg"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = rmD.memRM
	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val value = rmM.getValue()
		processor.flags.setFlag(FlagType.CARRY_FLAG, value != ULong.MIN_VALUE)
		rmM.setValue(0u - value)
		this.setFlagsForResult(processor, 0u - value)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}