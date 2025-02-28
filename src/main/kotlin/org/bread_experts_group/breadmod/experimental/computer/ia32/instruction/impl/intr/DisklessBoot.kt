package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x18u, 0x00u)
object DisklessBoot : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor): Unit = BootstrapLoader.handle(processor)
}