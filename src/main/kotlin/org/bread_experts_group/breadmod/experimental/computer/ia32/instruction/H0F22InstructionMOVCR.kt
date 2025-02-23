package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H0F22InstructionMOVCR : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		if (processor.operatingModeLocal != DecodingUtil.AddressingLength.R32) processor.bitOverride = true
		val (_, r) = processor.decoding.getModRMDisassembler(processor.cir).first
		return "cr0, $r"
	}

	override fun handle16(processor: IA32Processor) = this.handle32(processor)

	override fun getOperands32(processor: IA32Processor) = this.getOperands16(processor)
	override fun handle32(processor: IA32Processor) {
		if (processor.operatingModeLocal != DecodingUtil.AddressingLength.R32) processor.bitOverride = true
		val (rm) = processor.decoding.getModRM(processor.cir)
		processor.cr0.ex = rm.register.get()
	}

	override val supportsCodeSegmentOverride: Boolean = false
}