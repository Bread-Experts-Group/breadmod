package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object MultiplyModRM : Instruction("mul"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		when (processor.operandSize) {
			AddressingLength.R32 -> "edx:eax, eax [${hex(processor.a.tex)}] * ${it.regMem}"
			AddressingLength.R16 -> "dx:ax, ax [${hex(processor.a.tx)}] * ${it.regMem}"
			else                 -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = processor.a.ex * memRM.getRMi()
				processor.d.ex = result shr 32
				processor.a.ex = result
				processor.flags.setFlag(FlagType.OVERFLOW_FLAG, processor.d.ex > 0u)
				processor.flags.setFlag(FlagType.CARRY_FLAG, processor.d.ex > 0u)
			}
			AddressingLength.R16 -> {
				val result = processor.a.x * memRM.getRMs()
				processor.d.x = result shr 16
				processor.a.x = result
				processor.flags.setFlag(FlagType.OVERFLOW_FLAG, processor.d.x > 0u)
				processor.flags.setFlag(FlagType.CARRY_FLAG, processor.d.x > 0u)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}