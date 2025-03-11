package org.bread_experts_group.breadmod.experimental.computer.bios.h10

import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.InterruptReturn

class TeletypeOutput : BIOSInterruptProvider {
	companion object {
		const val ROWS: ULong = 80u
		const val COLS: ULong = 25u
		val ROWS_D: ULong = 160u
		val RES_D: ULong = this.ROWS * this.COLS
		const val COLOR_ADDR: ULong = 0xB8000u
	}

	private var x: ULong = 0u
	private var y: ULong = 0u

	override fun handle(processor: IA32Processor) {
		InterruptReturn.handle(processor)
		if (this.x == Companion.ROWS) {
			this.y++
			this.x = 0u
		}
		if (this.y == Companion.COLS) {
			for (pos in Companion.COLOR_ADDR .. Companion.COLOR_ADDR + (TeletypeOutput.RES_D * 2u) step 2) {
				processor.computer.setMemoryAt16(
					pos,
					processor.computer.requestMemoryAt16(pos + TeletypeOutput.ROWS_D)
				)
			}
			this.y--
		}
		if (processor.a.tl == (0x0Au).toUByte()) {
			this.y++
			return
		} else if (processor.a.tl == (0x0Du).toUByte()) {
			this.x = 0u
			return
		}
		processor.computer.setMemoryAt16(
			Companion.COLOR_ADDR + (((this.y * Companion.ROWS) + this.x) * 2u),
			((processor.a.l shl 8) or 0xFu).toUShort()
		)
		this.x++
	}
}