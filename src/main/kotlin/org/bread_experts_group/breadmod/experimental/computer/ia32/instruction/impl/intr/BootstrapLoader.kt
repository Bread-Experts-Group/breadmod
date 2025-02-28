package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x19u, 0x00u)
object BootstrapLoader : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.ip.rx = processor.resetVector
	}
}