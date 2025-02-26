package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import kotlin.reflect.KMutableProperty0

interface RegisterMemorySingleOperandInstruction : ZeroOperandInstruction {
	override fun handle(processor: IA32Processor) {
		val (m, r) = processor.decoding.getModRM(processor.decoding.readFetch(), this.rmRegisterType)
		this.handle(processor, m, r)
	}

	override fun getOperands(processor: IA32Processor): String {
		val rmByte = processor.decoding.readFetch()
		val saved = processor.ip.rx
		val rm = processor.decoding.getModRM(rmByte, this.rmRegisterType)
		processor.ip.rx = saved
		return this.getOperands(
			processor,
			rm,
			processor.decoding.getModRMDisassembler(rmByte, this.rmRegisterType)
		)
	}

	val rmRegisterType: RegisterType

	fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String
	fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>)
}