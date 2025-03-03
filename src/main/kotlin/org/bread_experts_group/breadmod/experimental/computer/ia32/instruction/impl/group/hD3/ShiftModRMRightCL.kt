package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hD3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.shr
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object ShiftModRMRightCL : Instruction("shl"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = "${processor.rmD().regMem}, cl [${hex(processor.c.tl)}]"
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = memRM.getRMi() shr processor.c.tl.toInt()
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = memRM.getRMs() shr processor.c.tl.toInt()
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
		TODO("Carry flag")
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}