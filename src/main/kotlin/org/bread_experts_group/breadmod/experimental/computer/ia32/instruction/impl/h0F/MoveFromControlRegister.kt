package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import kotlin.reflect.KMutableProperty0

/**
 * Opcode: `0F 20 /r` |
 * Instruction: `MOV r32, CR0–7` |
 * Flags Modified: `OF, SF, ZF, AF, PF, CF` (undefined)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x0F20u)
object MoveFromControlRegister : RegisterMemorySingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String = "mov"
	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM}, ${rmD.register}"

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		rmM.decide({ it.set(rmR.get()) }, { processor.computer.setMemoryAt32(it, rmR.get().toUInt()) })
	}

	override val rmRegisterType: RegisterType = RegisterType.CONTROL_REGISTER
}