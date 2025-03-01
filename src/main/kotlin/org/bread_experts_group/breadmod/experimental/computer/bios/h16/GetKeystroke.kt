package org.bread_experts_group.breadmod.experimental.computer.bios.h16

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

object GetKeystroke : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.a.tl = processor.computer.ioMap.getValue(0xB30D0000u).read()
		InterruptReturn.handle(processor)
	}
}