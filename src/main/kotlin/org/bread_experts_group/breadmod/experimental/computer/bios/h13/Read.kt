package org.bread_experts_group.breadmod.experimental.computer.bios.h13

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

object Read : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		val (primary, bootEntry) = processor.computer.disc!!.getBoot()
		val isoStart = (bootEntry.loadRBA * primary.logicalBlockSize).toULong()
		val loc = isoStart + (((processor.d.h * 18u) + (processor.c.l - 1u)) * 512u)
		processor.decoding.loadDiscIntoMemory(
			loc,
			loc + (processor.a.l * 512u),
			processor.es.offset(processor.b.x)
		)

		InterruptReturn.handle(processor)
//		processor.a.h = 0x00u
//		processor.flags.setFlag(FlagType.CARRY_FLAG, false)
		this.setError(processor, 0x04u)
	}
}