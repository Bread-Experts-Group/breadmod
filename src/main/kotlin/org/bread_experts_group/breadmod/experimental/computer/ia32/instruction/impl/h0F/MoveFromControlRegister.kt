package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

/**
 * Opcode: `0F 20 /r` |
 * Instruction: `MOV r32, CR0–7` |
 * Flags Modified: `OF, SF, ZF, AF, PF, CF` (undefined)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x0F20u)
object MoveFromControlRegister : Instruction("mov"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R32).let {
		"${it.regMem}, ${it.register}"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm(AddressingLength.R32)
		memRM.setRMi(register.get().toUInt())
	}

	override val registerType: RegisterType = RegisterType.CONTROL_REGISTER
}