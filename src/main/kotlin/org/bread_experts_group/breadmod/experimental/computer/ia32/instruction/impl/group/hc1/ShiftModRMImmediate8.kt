package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hc1

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryImmediate8DoubleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

class ShiftModRMImmediate8(val n: Char, val operation: (ULong, Int) -> ULong) :
	RegisterMemoryImmediate8DoubleOperandInstruction,
	LogicalArithmeticFlagOperations {
	override fun getMnemonic(
		processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult, imm8: UByte
	): String = "sh${this.n}"

	override fun getOperands(
		processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult, imm8: UByte
	): String = "${rmD.memRM}, ${hex(imm8)}"

	override fun handle(
		processor: IA32Processor, rmM: MemRMResult, rmD: KMutableProperty0<ULong>, imm8: UByte
	) {
		if (imm8 > 0u) {
			val saved = rmM.getValue()
			val result = this.operation(saved, imm8.toInt())
			rmM.setValue(result)
			this.setFlagsForResult(processor, result)
			processor.flags.setFlag(FlagType.CARRY_FLAG, (saved shr (imm8 - 1u).toInt() and 0x1u) > 0u)
		}
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}