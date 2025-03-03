package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hc1

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

class ShiftModRMImmediate8(
	n: Char,
	val op16: (UShort, Int) -> UShort,
	val op32: (UInt, Int) -> UInt
) : Instruction("sh$n"), ModRM, Immediate8, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		"${it.regMem}, ${hex(processor.imm8())}"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		val shiftCount = processor.imm8().toInt()
		if (shiftCount > 0) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					val saved32 = memRM.getRMi()
					val result = this.op32(saved32, shiftCount)
					memRM.setRMi(result)
					this.setFlagsForResult(processor, result)
					processor.flags.setFlag(
						FlagType.CARRY_FLAG,
						(saved32 and (1u shl 32 - (shiftCount))) > 0u
					)
				}
				AddressingLength.R16 -> {
					val saved16 = memRM.getRMs()
					val result = this.op16(saved16, shiftCount)
					memRM.setRMs(result)
					this.setFlagsForResult(processor, result)
					processor.flags.setFlag(
						FlagType.CARRY_FLAG,
						(saved16 and (1u shl 16 - (shiftCount)).toUShort()) > 0u
					)
				}
				else                 -> throw UnsupportedOperationException()
			}
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}