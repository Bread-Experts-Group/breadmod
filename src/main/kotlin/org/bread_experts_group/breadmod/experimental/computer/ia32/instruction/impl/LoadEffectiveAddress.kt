package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

@IA32Instruction(0x8Du)
object LoadEffectiveAddress : Instruction("lea"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		register.set(memRM.memory!!)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}