package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x13u, 0x0u)
object ResetDiskSystem : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		this.setOK(processor)
	}
}