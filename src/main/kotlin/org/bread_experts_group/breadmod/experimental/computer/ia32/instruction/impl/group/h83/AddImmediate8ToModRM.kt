package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h83

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryImmediate8DoubleOperandInstruction
import kotlin.reflect.KMutableProperty0

object AddImmediate8ToModRM : RegisterMemoryImmediate8DoubleOperandInstruction, ArithmeticAdditionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "add"
	override fun getOperands(
		processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult, imm8: UByte
	): String = "${rmD.memRM}, ${BinaryUtil.hex(imm8)}"

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmD: KMutableProperty0<ULong>, imm8: UByte) {
		val result = rmM.getValue() + imm8
		rmM.setValue(result)
		this.setFlagsForResult(processor, result)
		this.setFlagsForOperation(processor, rmM.getValue(), imm8.toUInt()) // TODO, conversion is bad
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}