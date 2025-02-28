package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.BreadMod.Companion.processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

@IA32Instruction(0x13u)
object AddWithCarryModRMToRegister : RegisterMemorySingleOperandInstruction, ArithmeticAdditionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "adc"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.register}, ${rmD.memRM}"

	private fun carryStat() = if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = this.setFlagsForOperationR(processor, rmR.get(), rmM.getValue().toUInt() + this.carryStat())
		rmR.set(result)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}