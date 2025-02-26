package org.bread_experts_group.breadmod.experimental.computer.io.ps2

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.io.IODevice

class PS2SystemControllerA : IODevice {
	var status: UByte = 0x2u

	override fun read(): UByte = status

	override fun write(d: UByte) {
		if (d.toUInt() == 0x2u) {
			status = d
		} else TODO(hex(d))
	}
	/*
	----------P0090009F--------------------------
	PORT 0090-009F – PS/2 – POS (PROGRAMMABLE OPTION SELECT)

	0090  ??  Central arbitration control port
	0090  RW  POST diagnostic code (most PS/2 with ISA bus)
	0091  R-  Card selection feedback
			  bit 0 set when adapter addressed and responds, cleared on read
	0092  RW  PS/2 system control port A  (port B is at PORT 0061h) (see #P0415)
	0094  -W  system board enable/setup register (see #P0416)
	0095  --  reserved
	0096  -W  adapter enable / setup register (see #P0417)
	0097  --  reserved

	Bitfields for PS/2 system control port A:
	Bit(s)	Description	(Table P0415)
	7-6	any bit set to 1 turns activity light on
	5	unused
	4	watchdog timout occurred
	3	=0 RTC/CMOS security lock (on password area) unlocked
		=1 CMOS locked (done by POST)
	2	unused
	1	A20 is active
	0	=0 system reset or write
		=1 pulse alternate reset pin (high-speed alternate CPU reset)
	Notes:	once set, bit 3 may only be cleared by a power-on reset
		on at least the C&T 82C235, bit 0 remains set through a CPU reset to
		  allow the BIOS to determine the reset method
		on the PS/2 30-286 & "Tortuga" the INT 15h/87h memory copy does
		  not use this port for A20 control, but instead uses the keyboard
		  controller (8042). Reportedly this may cause the system to crash
		  when access to the 8042 is disabled in password server mode
		  (see #P0398).
	SeeAlso: #P0416,#P0417,MSR 00001000h
	 */
}