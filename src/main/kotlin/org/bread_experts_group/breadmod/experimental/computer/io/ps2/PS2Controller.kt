package org.bread_experts_group.breadmod.experimental.computer.io.ps2

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.io.IODevice

class PS2Controller {
	val inputBuffer = mutableListOf<UByte>()
	val outputBuffer = mutableListOf<UByte>()
	var executor: ((UByte) -> Unit)? = null
	val data = object : IODevice {
		override fun read(): UByte = TODO("Not yet implemented")
		override fun write(d: UByte) {
			executor?.invoke(d)
			executor = null
		}
	}
	val command = object : IODevice {
		override fun read(): UByte {
			/*
			Bitfields for keyboard controller read status (ISA, EISA):
			Bit(s)	Description	(Table P0398)
			7	parity error on transmission from keyboard
			6	receive timeout
			5	transmit timeout
			4	keyboard interface inhibited by keyboard lock
				or by password server mode (IBM PS/2-286 [model bytes FCh/09h],
				  "Tortuga" [model F8h/19h]) (see #00515 at INT 15/AH=C0h)
			3	=1 data written to input register is command (PORT 0064h)
				=0 data written to input register is data (PORT 0060h)
			2	system flag status: 0=power up or reset	 1=selftest OK
			1	input buffer full (input 60/64 has data for 8042)
				no write access allowed until bit clears
			0	output buffer full (output 60 has data for system)
				bit is cleared after read access
			SeeAlso: PORT 0064h-R,#P0399,#P0400,#P0401
			 */
			// 0064 R- keyboard controller read status (see #P0398,#P0399,#P0400)
			return 0b00000110u
		}

		override fun write(d: UByte) = when (d.toUInt()) {
			0xD1u -> {
				executor = {
					println(it)
					// TODO Write output
				}
			}
			0xF0u, 0xF1u, 0xF2u, 0xF3u, 0xF4u, 0xF5u, 0xF6u, 0xF7u, 0xF8u, 0xF9u, 0xFAu, 0xFBu, 0xFCu, 0xFDu, 0xFEu,
			0xFFu -> {
				// TODO, pulsing doesn't exist...
			}
			else  -> TODO("Unknown PS/2 command: ${hex(d)}")
		}
		// 0064 -W keyboard controller input buffer (ISA, EISA) (see #P0401)
	}
}