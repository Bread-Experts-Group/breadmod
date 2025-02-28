package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x16u, 0x00u)
object GetKeystroke : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.a.tl = processor.computer.ioMap.getValue(0xB30D0000u).read()
	}
}