package org.bread_experts_group.breadmod.experimental.computer.ia32.register

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

class SegmentRegister(val processor: IA32Processor, name: String, rx: ULong) : Register(processor.logger, name, rx) {
	fun offset(o: ULong): ULong =
		if (this.processor.gdtrBase.rx > 0u && this.rx > 0u) {
			val descriptor = this.readSegmentDescriptor()
			val offset = descriptor.base + o
//			if (offset > descriptor.limit * (if ((descriptor.flags and 0b1000u) > 0u) 4096u else 1u))
//				throw TODO("GDT limit exceeded")
			offset
		} else (this.rx * 0x10u) + o

	fun offset(r: Register): ULong = this.offset(r.rx)
	fun hex(o: ULong): String = "${BinaryUtil.hex(this.tx)}:${BinaryUtil.hex(o).substring(2)}"
	fun hex(r: Register): String = this.hex(r.rx)

	class SegmentDescriptor(
		val limit: UInt, // TODO, limit check.
		val base: UInt,
		val access: UInt,
		val flags: UInt
	)

	fun readSegmentDescriptor(): SegmentDescriptor {
		val offset = this.processor.gdtrBase.rx + this.rx
		if (offset > this.processor.gdtrBase.rx + this.processor.gdtrLimit.rx) TODO("EXCEPTION: GDTR OUT OF BOUNDS")
		val data = this.processor.computer.requestMemoryAt64(offset)
		return SegmentDescriptor(
			((data and 0x00_0_0_00_00_0000_FFFFu) or ((data and 0x00_0_F_00_00_0000_0000u) shr 32)).toUInt(),
			(((data and 0x00_0_0_00_FF_FFFF_0000u) shr 16) or ((data and 0xFF_0_0_00_00_0000_0000u) shr 56)).toUInt(),
			((data and 0x00_0_0_FF_00_0000_0000u) shr 40).toUInt(),
			(((data and 0x00_F_0_00_00_0000_0000u) shr 52)).toUInt()
		)
	}
}