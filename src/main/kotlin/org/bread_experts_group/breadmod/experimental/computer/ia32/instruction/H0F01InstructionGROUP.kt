package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H0F01InstructionGROUP : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun handle32(processor: IA32Processor) {
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		when (r) {
			0u   -> TODO("SGDT/VMCALL/VMLAUNCH/VMRESUME/VMXOFF")
			1u   -> when (processor.cir.toUInt()) {
				0xC8u -> TODO("MONITOR")
				0xC9u -> TODO("MWAIT")
				else  -> {
					val addr = rm.memRM.address.get()
					processor.computer.setMemoryAt16(addr, processor.idtrLimit.tx)
					processor.computer.setMemoryAt32(addr + 2u, processor.idtrBase.tex)
					processor.logger.warn(
						"IDT save, {} [{}] -> {}",
						hex(processor.idtrBase.tex), hex(processor.idtrLimit.tx),
						hex(addr)
					)
				}
			}
			2u   -> when (processor.cir.toUInt()) {
				0xD0u -> TODO("XGETBV")
				0xD1u -> TODO("XSETBV")
				else  -> {
					val addr = rm.memRM.address.get()
					processor.gdtrLimit.tx = processor.computer.requestMemoryAt16(addr)
					processor.gdtrBase.tex = processor.computer.requestMemoryAt32(addr + 2u)
					processor.logger.warn(
						"GDT load, {} [{}] <- {}",
						hex(processor.gdtrBase.tex), hex(processor.gdtrLimit.tx),
						hex(addr)
					)
				}
			}
			3u   -> {
				val addr = rm.memRM.address.get()
				processor.idtrLimit.tx = processor.computer.requestMemoryAt16(addr)
				processor.idtrBase.tex = processor.computer.requestMemoryAt32(addr + 2u)
				processor.logger.warn(
					"IDT load, {} [{}] <- {}",
					hex(processor.idtrBase.tex), hex(processor.idtrLimit.tx),
					hex(addr)
				)
			}
			4u   -> TODO("SMSW")
			6u   -> TODO("LMSW")
			7u   -> TODO("INVLPG/RDTSCP")
			else -> throw IllegalStateException("/$r")
		}
	}

	override val supportsCodeSegmentOverride: Boolean = false
}