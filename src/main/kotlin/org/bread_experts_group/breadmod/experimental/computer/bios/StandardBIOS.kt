package org.bread_experts_group.breadmod.experimental.computer.bios

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.bios.h10.TeletypeOutput
import org.bread_experts_group.breadmod.experimental.computer.bios.h13.ExtendedRead
import org.bread_experts_group.breadmod.experimental.computer.bios.h13.InstallationCheck
import org.bread_experts_group.breadmod.experimental.computer.bios.h13.Read
import org.bread_experts_group.breadmod.experimental.computer.bios.h13.ResetDiskSystem
import org.bread_experts_group.breadmod.experimental.computer.bios.h16.GetKeystroke
import org.bread_experts_group.breadmod.experimental.computer.bios.h19.BootstrapLoader
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

class StandardBIOS : BIOSProvider {
	val teletype: TeletypeOutput = TeletypeOutput()

	override fun initialize(computer: Computer) {
		val processor = computer.processor as IA32Processor
		// See Intel® Platform Innovation Framework for UEFI, Compatibility Support Module Specification, Rev 0.98
		// Section 5.2, Table 14, "Fixed BIOS Entry Points", Page 162.
		// Write Real Mode Interrupt Vector Table
		for (offset in ULong.MIN_VALUE .. 255u) processor.computer.setMemoryAt32((offset * 4u), 0xF000FF50u)
		// (F000:FF50) Unknown INT Reset
		processor.setHook(0xF000u, 0xFF50u) {
			throw IllegalArgumentException("Bad interrupt")
		}
		// (F000:E6F2) INT 0x19 Entry Point
		processor.computer.setMemoryAt32(0x0064u, 0xF000E6F2u)
		processor.setHook(0xF000u, 0xE6F2u) {
			when (processor.a.h.toUInt()) {
				0x00u -> BootstrapLoader
				else  -> throw IllegalArgumentException("Unknown 0x19 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:E82E) INT 0x16 Entry Point
		processor.computer.setMemoryAt32(0x0058u, 0xF000E82Eu)
		processor.setHook(0xF000u, 0xE82Eu) {
			when (processor.a.h.toUInt()) {
				0x00u -> GetKeystroke
				else  -> throw IllegalArgumentException("Unknown 0x16 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:EC59) INT 0x13 Floppy Entry Point
		processor.computer.setMemoryAt32(0x004Cu, 0xF000EC59u)
		processor.setHook(0xF000u, 0xEC59u) {
			when (processor.a.h.toUInt()) {
				0x00u -> ResetDiskSystem
				0x02u -> Read
				0x41u -> InstallationCheck
				0x42u -> ExtendedRead
				else  -> throw IllegalArgumentException("Unknown 0x13 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:F065) INT 0x10 Video Entry Point
		processor.computer.setMemoryAt32(0x0040u, 0xF000F065u)
		processor.setHook(0xF000u, 0xF065u) {
			when (processor.a.h.toUInt()) {
				0x0Eu -> teletype
				else  -> throw IllegalArgumentException("Unknown 0x10 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:FF53) "Dummy Interrupt Handler"
		processor.computer.setMemoryAt((0xF000u * 0x10u) + 0xFF53u, 0xCFu)
		// (F000:FFF0) "Power-On Entry Point" / Reset Vector
		processor.setHook(0xF000u, 0xFFF0u) {
			val disc = computer.disc ?: return@setHook
			val (primary, entry) = disc.getBoot()
			val discStart = (entry.loadRBA.toLong() * primary.logicalBlockSize).toULong()
			val memoryStart = (entry.loadSegment * 0x10).toULong()
			it.decoding.loadDiscIntoMemory(
				discStart,
				discStart + (entry.sectorCount * primary.logicalBlockSize).toULong(),
				memoryStart
			)
			it.d.l = 0xE0u
			it.cs.rx = 0u
			it.ip.rx = memoryStart
		}
		// (F000:FF54) INT 0x05 Print Screen Entry Point
		// TODO INT 05
	}
}