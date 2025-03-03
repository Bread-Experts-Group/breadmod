package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32Instruction(0xACu)
object LoadByteIntoAL : Instruction("lods") {
	override fun operands(processor: IA32Processor): String = "al, ${processor.segmentOverride.name}:si"
	override fun handle(processor: IA32Processor) {
		processor.a.tl = processor.computer.requestMemoryAt(processor.segmentOverride.offset(processor.si.x))
		processor.si.x = (if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) ULong::minus else ULong::plus)(
			processor.si.x,
			1u
		)
	}
}