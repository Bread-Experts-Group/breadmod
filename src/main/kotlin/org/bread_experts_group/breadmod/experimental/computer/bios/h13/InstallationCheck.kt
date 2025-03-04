package org.bread_experts_group.breadmod.experimental.computer.bios.h13

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

object InstallationCheck : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		InterruptReturn.handle(processor)
		this.setOK(processor)
		processor.b.tx = 0xAA55u
		processor.a.th = 0x30u
		processor.c.tx = 0b111u
	}
}