package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HCDInstructionINT : Instruction {
	override fun handle(processor: IA32Processor) {
		val index = processor.fetch().let { processor.cir.toInt() }
		processor.logger.warn("INTERRUPT ${hex(index)}")
		if (index == 0x13) {
			val offset = processor.ds.offset(processor.si)
			// This is reading incorrectly...
			// pls pet me when i wake up
			// :pleading_face:
			val copySectors = processor.computer.requestMemoryAt16(offset + 2u)
			val toAddress = ((processor.computer.requestMemoryAt16(offset + 6u) * 0x10u) +
					processor.computer.requestMemoryAt16(offset + 4u)).toULong()
			val fromDiscLBA = processor.computer.requestMemoryAt48(offset + 8u).toLong()
			processor.computer.disc?.let { disc ->
				val (primary, entry) = disc.getBoot()
				val size = copySectors * primary.logicalBlockSize.toULong()
				val discStart = (entry.loadRBA.toLong() * primary.logicalBlockSize) + ((fromDiscLBA - 1) * 512)
				val savedPosition = disc.discStream.channel.position()
				disc.discStream.channel.position(discStart)
				processor.logger.warn(
					"BIOS DISC CPY ${hex(discStart)} -> ${hex(discStart.toULong() + size)} @ ${hex(toAddress)}"
				)
				for (offset in toAddress .. toAddress + size) {
					// TODO Send in chunks
					processor.computer.setMemoryAt(offset, disc.discStream.read().toUByte())
				}
				disc.discStream.channel.position(savedPosition)
				processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false)
				processor.a.h = 0x00u
				return
			}
			processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, true)
			processor.a.h = 0x04u
		} else if (index == 0x10) {
			when (processor.a.h.toUInt()) {
				0x0Eu -> processor.logger.warn("TELETYPE: " + Char(processor.a.l.toUShort()))
				else  -> TODO("BIOS Video mode control ...")
			}
		} else TODO("This interr**upt")
	}
}