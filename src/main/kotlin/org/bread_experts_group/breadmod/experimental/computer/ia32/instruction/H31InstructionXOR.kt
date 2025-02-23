package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H31InstructionXOR : LogicalArithmeticInstruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		val (f, s) = processor.decoding.getModRMDisassembler(processor.cir).first
		return "$s, $f"
	}

	override fun handle16(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		val result = rm.memRM.decide(
			{ (it.get() xor rm.register.get()).also(it::set) },
			{
				(processor.computer.requestMemoryAt16(it) xor rm.register.get().toUShort()).also { r ->
					processor.computer.setMemoryAt16(it, r)
				}.toULong()
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override fun getOperands32(processor: IA32Processor): String = getOperands16(processor)

	override fun handle32(processor: IA32Processor) {
		val (rm) = processor.decoding.getModRM(processor.cir)
		val result = rm.memRM.decide(
			{ (it.get() xor rm.register.get()).also(it::set) },
			{
				(processor.computer.requestMemoryAt32(it) xor rm.register.get().toUInt()).toULong().also { r ->
					processor.computer.setMemoryAt32(it, r.toUInt())
				}
			}
		)
		this.setFlagsToResult(processor, result)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}