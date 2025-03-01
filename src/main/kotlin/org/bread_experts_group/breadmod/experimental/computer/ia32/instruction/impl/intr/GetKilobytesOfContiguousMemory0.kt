package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x12u, 0x00u)
object GetKilobytesOfContiguousMemory0 : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.a.tx = 0x27Fu
	}
}