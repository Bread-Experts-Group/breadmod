package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import kotlin.reflect.KMutableProperty0

@IA32Instruction(0x0FB6u)
object MoveWithZeroExtension : RegisterMemorySingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "movzx"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.register}, ${rmD.memRM}"

	override fun handle(processor: IA32Processor, rmM: DecodingUtil.MemRMResult, rmR: KMutableProperty0<ULong>) {
		rmR.set(rmM.getValue() and 0xFFu)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}