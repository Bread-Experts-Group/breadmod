package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x13u, 0x41u)
object InstallationCheck : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		this.setOK(processor)
		processor.b.tx = 0xAA55u
		processor.a.th = 0x30u
		processor.c.tx = 0b111u
	}
}