package org.bread_experts_group.breadmod.experimental.computer.bios.h10

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

class TeletypeOutput : BIOSInterruptProvider {
	companion object {
		const val ROWS: Int = 40
		const val COLS: Int = 25
	}

	private var x: Int = 0
	private var y: Int = 0

	override fun handle(processor: IA32Processor) {
		InterruptReturn.handle(processor)
		if (processor.a.tl == (0x0D).toUByte()) return
		if (processor.a.tl == (0x0Au).toUByte() || (this.x == Companion.ROWS)) {
			this.y++
			this.x = 0
			if (processor.a.tl == (0x0Au).toUByte()) return
		}

		if (this.y == Companion.COLS) {
			this.y--
			for (ly in 0 ..< TeletypeOutput.COLS) {
				for (lx in 0 ..< TeletypeOutput.ROWS) {
					processor.computer.setMemoryAt16(
						0xB8000u + ((ly * Companion.ROWS * 2) + (lx * 2)).toULong(),
						if (ly == TeletypeOutput.COLS - 1) 0u
						else processor.computer.requestMemoryAt16(
							0xB8000u + (((ly + 1) * Companion.ROWS * 2) + (lx * 2)).toULong()
						)
					)
				}
			}
		}
		processor.computer.setMemoryAt16(
			0xB8000u + ((this.y * Companion.ROWS * 2) + (this.x * 2)).toULong(),
			((processor.a.l shl 8) or 0xFu).toUShort()
		)
		this.x++
	}
}