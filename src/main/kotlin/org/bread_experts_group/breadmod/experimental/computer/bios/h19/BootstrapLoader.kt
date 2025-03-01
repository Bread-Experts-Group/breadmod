package org.bread_experts_group.breadmod.experimental.computer.bios.h19

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object BootstrapLoader : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.computer.reset()
		processor.fetch()
		processor.ip.rx--
	}
}