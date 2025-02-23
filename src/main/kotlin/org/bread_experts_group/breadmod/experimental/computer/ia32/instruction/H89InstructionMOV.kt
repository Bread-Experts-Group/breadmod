package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H89InstructionMOV : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		val (f, s) = processor.decoding.getModRMDisassembler(processor.cir).first
		return "$f, $s"
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.memRM.decide(
			{ it.set(rm.register.get()) },
			{
				val physical = processor.ds.offset(it)
				processor.computer.setMemoryAt16(physical, rm.register.get().toUShort())
			}
		)
	}

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		rm.memRM.decide(
			{ it.set(rm.register.get()) },
			{
				val physical = processor.ds.offset(it)
				processor.computer.setMemoryAt32(physical, rm.register.get().toUInt())
			}
		)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}