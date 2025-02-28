package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemory8SingleOperandInstruction
import kotlin.reflect.KMutableProperty0

@IA32Instruction(0x86u)
object ExchangeModRMWithRegister8 : RegisterMemory8SingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "xchg"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.register}, ${rmD.memRM}"

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val tmp = rmM.getValue()
		rmM.setValue(rmR.get())
		rmR.set(tmp)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}