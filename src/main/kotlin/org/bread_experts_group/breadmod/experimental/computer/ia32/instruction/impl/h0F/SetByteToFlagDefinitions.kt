package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32InstructionCluster
class SetByteToFlagDefinitions(processor: IA32Processor) {
	class SetByteToFlag(val condition: ((FlagType) -> Boolean) -> Boolean, n: String) : Instruction("set$n"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).regMem
		override fun handle(processor: IA32Processor) {
			val (memRm, _) = processor.rm(AddressingLength.R8)
			memRm.setRMb(if (this.condition(processor.flags::getFlag)) 1u else 0u)
		}

		override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
	}

	@IA32Instruction(0x0F90u)
	val o: SetByteToFlag = SetByteToFlag({ it(FlagType.OVERFLOW_FLAG) }, "o")

	@IA32Instruction(0x0F91u)
	val no: SetByteToFlag = SetByteToFlag({ !it(FlagType.OVERFLOW_FLAG) }, "no")

	@IA32Instruction(0x0F92u)
	val c: SetByteToFlag = SetByteToFlag({ it(FlagType.CARRY_FLAG) }, "c")

	@IA32Instruction(0x0F93u)
	val nc: SetByteToFlag = SetByteToFlag({ !it(FlagType.CARRY_FLAG) }, "nc")

	@IA32Instruction(0x0F94u)
	val z: SetByteToFlag = SetByteToFlag({ it(FlagType.ZERO_FLAG) }, "z")

	@IA32Instruction(0x0F95u)
	val nz: SetByteToFlag = SetByteToFlag({ !it(FlagType.ZERO_FLAG) }, "nz")

	@IA32Instruction(0x0F96u)
	val be: SetByteToFlag = SetByteToFlag({ it(FlagType.CARRY_FLAG) || it(FlagType.ZERO_FLAG) }, "be")

	@IA32Instruction(0x0F97u)
	val nbe: SetByteToFlag = SetByteToFlag({ !it(FlagType.CARRY_FLAG) && !it(FlagType.ZERO_FLAG) }, "nbe")

	@IA32Instruction(0x0F98u)
	val s: SetByteToFlag = SetByteToFlag({ it(FlagType.SIGN_FLAG) }, "s")

	@IA32Instruction(0x0F99u)
	val ns: SetByteToFlag = SetByteToFlag({ !it(FlagType.SIGN_FLAG) }, "ns")

	@IA32Instruction(0x0F9Au)
	val p: SetByteToFlag = SetByteToFlag({ it(FlagType.PARITY_FLAG) }, "p")

	@IA32Instruction(0x0F9Bu)
	val np: SetByteToFlag = SetByteToFlag({ !it(FlagType.PARITY_FLAG) }, "np")

	@IA32Instruction(0x0F9Cu)
	val l: SetByteToFlag = SetByteToFlag({ it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG) }, "l")

	@IA32Instruction(0x0F9Du)
	val nl: SetByteToFlag = SetByteToFlag({ it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG) }, "nl")

	@IA32Instruction(0x0F9Eu)
	val le: SetByteToFlag = SetByteToFlag({
		it(FlagType.ZERO_FLAG) || (it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG))
	}, "le")

	@IA32Instruction(0x0F9Fu)
	val nle: SetByteToFlag = SetByteToFlag({
		!it(FlagType.ZERO_FLAG) && (it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG))
	}, "nle")
}