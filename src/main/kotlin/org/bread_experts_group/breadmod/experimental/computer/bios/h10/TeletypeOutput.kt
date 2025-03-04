package org.bread_experts_group.breadmod.experimental.computer.bios.h10

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

object TeletypeOutput : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		InterruptReturn.handle(processor)
		val char = Char(processor.a.l.toUShort())
		if (char == '\r') return
		processor.computer.buffer += char
	}
}