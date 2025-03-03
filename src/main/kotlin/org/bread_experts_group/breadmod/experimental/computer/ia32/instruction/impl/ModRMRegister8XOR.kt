package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

/**
 * Opcode: `30 /r` |
 * Instruction: `XOR r/m8, r8` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x30u)
object ModRMRegister8XOR : Instruction("xor"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
		"${it.regMem}, ${it.register}"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm(AddressingLength.R8)
		val result = memRM.getRMb() xor register.get().toUByte()
		memRM.setRMb(result)
		this.setFlagsForResult(processor, result)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}