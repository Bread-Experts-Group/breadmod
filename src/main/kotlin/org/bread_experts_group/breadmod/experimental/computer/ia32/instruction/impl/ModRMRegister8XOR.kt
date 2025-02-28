package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemory8SingleOperandInstruction
import kotlin.reflect.KMutableProperty0

/**
 * Opcode: `30 /r` |
 * Instruction: `XOR r/m8, r8` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x30u)
object ModRMRegister8XOR : RegisterMemory8SingleOperandInstruction, LogicalArithmeticFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "xor"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, ${rmD.register}"

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val result = rmM.getValue() xor rmR.get()
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}