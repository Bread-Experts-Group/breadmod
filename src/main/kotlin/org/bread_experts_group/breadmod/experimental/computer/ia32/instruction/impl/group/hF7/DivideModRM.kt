package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object DivideModRM : Instruction("div"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		when (processor.operandSize) {
			AddressingLength.R32 -> "edx:eax, ${it.regMem}"
			AddressingLength.R16 -> "dx:ax, ${it.regMem}"
			else                 -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val dividend = ((processor.d.ex shl 32) or processor.a.ex)
				processor.a.ex = dividend / memRM.getRMi()
				processor.d.ex = dividend % memRM.getRMi()
			}
			AddressingLength.R16 -> {
				val dividend = ((processor.d.x shl 16) or processor.a.x)
				processor.a.x = dividend / memRM.getRMs()
				processor.d.x = dividend % memRM.getRMs()
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}