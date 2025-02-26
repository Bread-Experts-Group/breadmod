package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

@BIOSInterrupt(0x13u, 0x42u)
object ExtendedRead : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		if (processor.d.tl != (0xE0u).toUByte()) {
			this.setError(processor, 0x04u)
			return
		}
		val (primary, _) = (processor.computer.disc ?: return).getBoot()
		val dapAddr = processor.ds.offset(processor.si)
		val lbs = primary.logicalBlockSize.toULong()
		val lba = processor.computer.requestMemoryAt64(dapAddr + 8u) * lbs
		processor.decoding.loadDiscIntoMemory(
			lba,
			lba + (processor.computer.requestMemoryAt16(dapAddr + 2u) * lbs),
			((processor.computer.requestMemoryAt16(dapAddr + 6u) * 0x10u) +
					processor.computer.requestMemoryAt16(dapAddr + 4u)).toULong()
		)
		this.setOK(processor)
	}
}