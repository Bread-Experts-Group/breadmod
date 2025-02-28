package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h81

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryImmediateOperatingLengthDoubleOperandInstruction
import kotlin.reflect.KMutableProperty0

object CompareImmediateToModRM : RegisterMemoryImmediateOperatingLengthDoubleOperandInstruction,
	ArithmeticSubtractionFlagOperations {
	override fun getMnemonic(processor: IA32Processor): String = "cmp"
	override fun getOperands16(
		processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult, imm16: UShort
	): String = "${rmD.memRM}, ${hex(imm16)}"

	override fun getOperands32(
		processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult, imm32: UInt
	): String = "${rmD.memRM}, ${hex(imm32)}"

	override fun handle16(
		processor: IA32Processor, rmM: MemRMResult, rmD: KMutableProperty0<ULong>, imm16: UShort
	) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), imm16)
		this.setFlagsForResult(processor, result)
	}

	override fun handle32(
		processor: IA32Processor, rmM: MemRMResult, rmD: KMutableProperty0<ULong>, imm32: UInt
	) {
		val result = this.setFlagsForOperationR(processor, rmM.getValue(), imm32)
		this.setFlagsForResult(processor, result)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}