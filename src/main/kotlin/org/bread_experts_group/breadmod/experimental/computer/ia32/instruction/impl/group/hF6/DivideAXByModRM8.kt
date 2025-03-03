package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF6

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object DivideAXByModRM8 : Instruction("div"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).regMem

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm(AddressingLength.R8)
		val savedR = processor.a.x
		val savedD = memRM.getRMb()
		processor.a.l = savedR / savedD
		processor.a.h = savedR % savedD
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}