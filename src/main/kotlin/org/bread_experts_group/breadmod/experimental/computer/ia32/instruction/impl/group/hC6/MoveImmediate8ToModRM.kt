package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hC6

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryImmediate8DoubleOperandInstruction
import kotlin.reflect.KMutableProperty0

object MoveImmediate8ToModRM : RegisterMemoryImmediate8DoubleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "mov"
	override fun getOperands(
		processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult, imm8: UByte
	): String = "${rmD.memRM}, ${hex(imm8)}"

	override fun handle(
		processor: IA32Processor, rmM: MemRMResult, rmD: KMutableProperty0<ULong>, imm8: UByte
	) {
		rmM.setValue(imm8.toULong())
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}