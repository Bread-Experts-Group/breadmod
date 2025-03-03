package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32Instruction(0x0FB7u)
object MoveWithZeroExtension16 : Instruction("movzx"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R32).let {
		"${it.register}, ${it.regMem}"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm(AddressingLength.R32)
		register.set(memRM.getRMs().toULong())
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}