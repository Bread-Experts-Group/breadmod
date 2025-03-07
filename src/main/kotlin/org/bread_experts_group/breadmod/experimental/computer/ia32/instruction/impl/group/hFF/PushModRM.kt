package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

object PushModRM : Instruction("push"), ModRM {
	override fun operands(processor: IA32Processor): String {
		val saved = processor.ip.rx
		val o1 = processor.rmD().regMem
		processor.ip.rx = saved
		val memRM = processor.rm().memRM
		return o1 + when (processor.operandSize) {
			AddressingLength.R32 -> " [${hex(memRM.getRMi())}]"
			AddressingLength.R16 -> " [${hex(memRM.getRMs())}]"
			else                 -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> processor.push32(memRM.getRMi())
			AddressingLength.R16 -> processor.push16(memRM.getRMs())
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}