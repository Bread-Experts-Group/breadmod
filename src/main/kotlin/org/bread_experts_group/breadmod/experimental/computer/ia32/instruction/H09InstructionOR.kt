package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H09InstructionOR : LogicalArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands32(processor: IA32Processor): String {
		prepare(processor)
		val (f, s) = processor.decoding.getModRMDisassembler(processor.cir).first
		return "$s, $f"
	}

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		val result = rm.memRM.register.get().get() or rm.register.get()
		rm.register.set(result)
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}