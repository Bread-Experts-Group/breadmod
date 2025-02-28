package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import kotlin.reflect.KMutableProperty0

/**
 * Opcode: `33 /r` |
 * Instruction: `XOR r(16/32), r/m(16/32)` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x33u)
object RegisterModRMXOR : RegisterMemorySingleOperandInstruction, LogicalArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "xor"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.register}, ${rmD.memRM}"

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = rmR.get() xor rmM.getValue()
		rmR.set(result)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}