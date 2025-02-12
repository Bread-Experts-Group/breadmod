package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object HCDInstructionINT {
	fun handle(processor: IA32Processor) {
		val index = processor.fetch().let { processor.cir.toInt() }
		processor.logger.warn("INTERRUPT ${hex(index)}")
		processor.logger.warn("A :" + hex(processor.a.rx))
		processor.logger.warn("B :" + hex(processor.b.rx))
		processor.logger.warn("C :" + hex(processor.c.rx))
		processor.logger.warn("D :" + hex(processor.d.rx))
		processor.logger.warn("SP:" + hex(processor.sp.rx))
		processor.logger.warn("BP:" + hex(processor.bp.rx))
		processor.logger.warn("DI:" + hex(processor.di.rx))
		processor.logger.warn("SI:" + hex(processor.si.rx))
		processor.logger.warn("CS:" + hex(processor.cs.rx))
		processor.logger.warn("DS:" + hex(processor.ds.rx))
		processor.logger.warn("SS:" + hex(processor.ss.rx))
		processor.logger.warn("ES:" + hex(processor.es.rx))
		processor.logger.warn("FS:" + hex(processor.fs.rx))
		processor.logger.warn("GS:" + hex(processor.gs.rx))
		if (index == 0x13) {
			val offset = (processor.ds.ex * 0x10u) + processor.si.ex
			processor.logger.warn("13 S:" + hex(processor.computer.requestMemoryAt(offset)))
			processor.logger.warn("SC  :" + hex(processor.computer.requestMemoryAt16(offset + 2u)))
			processor.logger.warn("SEG :" + hex(processor.computer.requestMemoryAt16(offset + 4u)))
			processor.logger.warn("OFF :" + hex(processor.computer.requestMemoryAt16(offset + 6u)))
			processor.logger.warn("LBA :" + hex(processor.computer.requestMemoryAt64(offset + 8u)))
			processor.setFlag(IA32Processor.FlagType.CARRY_FLAG, false)
			processor.a.h = 0x00u
		} else TODO("This interrupt")
	}
}