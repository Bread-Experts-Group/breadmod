package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.Instruction
import kotlin.reflect.KMutableProperty0

interface RegisterMemorySingleOperandInstruction : Instruction {
	override fun handle(processor: IA32Processor) {
		val (m, r) = processor.decoding.getModRM(processor.decoding.readFetch(), this.rmRegisterType)
		this.handle(processor, m, r)
	}

	override fun getMnemonic(processor: IA32Processor): String {
		val rmByte = processor.decoding.readFetch()
		val saved = processor.ip.rx
		val rm = processor.decoding.getModRM(rmByte, this.rmRegisterType)
		processor.ip.rx = saved
		return this.getMnemonic(
			processor, rm,
			processor.decoding.getModRMDisassembler(rmByte, this.rmRegisterType)
		)
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

	fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String
	fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String
	fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>)
}