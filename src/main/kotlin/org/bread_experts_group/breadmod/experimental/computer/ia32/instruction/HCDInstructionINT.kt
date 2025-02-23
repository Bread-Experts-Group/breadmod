package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

object HCDInstructionINT : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getOperands16(processor: IA32Processor): String {
		prepare(processor)
		return hex(processor.cir)
	}

	override fun handle16(processor: IA32Processor) {
		when (processor.cir.toUInt()) {
			0x13u -> {
				val offset = processor.ds.offset(processor.si)
				val copySectors = processor.computer.requestMemoryAt16(offset + 2u)
				val toAddress = ((processor.computer.requestMemoryAt16(offset + 6u) * 0x10u) +
						processor.computer.requestMemoryAt16(offset + 4u)).toULong()
				val fromDiscLBA = processor.computer.requestMemoryAt48(offset + 8u).toLong()
				processor.computer.disc?.let { disc ->
					val (primary, _) = disc.getBoot()
					val size = copySectors * primary.logicalBlockSize.toULong()
					val discStart = fromDiscLBA * 2048
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
					processor.flags.setFlag(FlagType.CARRY_FLAG, false)
					processor.a.h = 0x00u
					return
				}
				processor.flags.setFlag(FlagType.CARRY_FLAG, true)
				processor.a.h = 0x04u
			}
			0x10u -> {
				when (processor.a.h.toUInt()) {
					0x0Eu -> processor.logger.warn("TELETYPE: " + Char(processor.a.l.toUShort()))
					else -> TODO("BIOS Video mode control ... ${processor.a.h.toUInt()}")
				}
			}
			0x15u -> {
				when (processor.a.h.toUInt()) {
					else -> TODO("BIOS Misc ... ${processor.a.h.toUInt()}")
				}
			}
			else  -> TODO("Unknown interrupt.")
		}
	}

	override val supportsCodeSegmentOverride: Boolean = false
}