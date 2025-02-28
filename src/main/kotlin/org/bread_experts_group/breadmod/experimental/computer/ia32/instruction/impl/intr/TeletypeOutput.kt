package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.client.gui.IA32ComputerOutputOverlay
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x10u, 0x0Eu)
object TeletypeOutput : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		val char = Char(processor.a.l.toUShort())
		if (char == '\r') return
		IA32ComputerOutputOverlay.output += char
	}
}