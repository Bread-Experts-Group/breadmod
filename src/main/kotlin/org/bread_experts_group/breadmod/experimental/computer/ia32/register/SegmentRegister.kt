package org.bread_experts_group.breadmod.experimental.computer.ia32.register

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

class SegmentRegister(val processor: IA32Processor, name: String, rx: ULong) : Register(processor.logger, name, rx) {
	fun offset(o: ULong): ULong =
		if (processor.gdtrBase.rx > 0u) readSegmentDescriptor().base + o
		else (rx * 0x10u) + o

	fun offset(r: Register): ULong = this.offset(r.rx)
	fun hex(o: ULong): String = "${BinaryUtil.hex(this.tx)}:${BinaryUtil.hex(o).substring(2)}"
	fun hex(r: Register): String = this.hex(r.rx)

	class SegmentDescriptor(
		val limit: UInt, // TODO, limit check.
		val base: UInt,
		val flags: UInt // TODO, segment flags.
	)

	fun readSegmentDescriptor(): SegmentDescriptor {
		val offset = processor.gdtrBase.rx + rx
		if (offset > processor.gdtrBase.rx + processor.gdtrLimit.rx) TODO("EXCEPTION: GDTR OUT OF BOUNDS")
		val data = processor.computer.requestMemoryAt64(offset)
		return SegmentDescriptor(
			((data and 0x00_0_0_00_00_0000_FFFFu) or ((data and 0x00_0_F_00_00_0000_0000u) shr 32)).toUInt(),
			(((data and 0x00_0_0_00_FF_FFFF_0000u) shr 16) or ((data and 0xFF_0_0_00_00_0000_0000u) shr 56)).toUInt(),
			(((data and 0x00_0_0_FF_00_0000_0000u) shr 40) or ((data and 0x00_F_0_00_00_0000_0000u) shr 48)).toUInt()
		)
	}
}