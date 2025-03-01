package org.bread_experts_group.breadmod.experimental.computer.bios.h13

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

object ResetDiskSystem : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		InterruptReturn.handle(processor)
		this.setOK(processor)
	}
}