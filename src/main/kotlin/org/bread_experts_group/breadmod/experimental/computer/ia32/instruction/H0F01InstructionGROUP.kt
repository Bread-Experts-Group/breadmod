package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H0F01InstructionGROUP : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	override fun getMnemonic(processor: IA32Processor): String {
		prepare(processor)
		val (_, r) = processor.decoding.getModRM(processor.cir)
		return when (r) {
			0u   -> TODO("SGDT/VMCALL/VMLAUNCH/VMRESUME/VMXOFF")
			1u   -> when (processor.cir.toUInt()) {
				0xC8u -> TODO("MONITOR")
				0xC9u -> TODO("MWAIT")
				else  -> "sidt"
			}
			2u   -> when (processor.cir.toUInt()) {
				0xD0u -> TODO("XGETBV")
				0xD1u -> TODO("XSETBV")
				else  -> "lgdt"
			}
			3u   -> "lidt"
			4u   -> TODO("SMSW")
			6u   -> TODO("LMSW")
			7u   -> TODO("INVLPG/RDTSCP")
			else -> throw IllegalStateException("/$r")
		}
	}

	override fun getOperands32(processor: IA32Processor): String {
		prepare(processor)
		val (rm, r) = processor.decoding.getModRM(processor.cir)
		when (r) {
			0u   -> TODO("SGDT/VMCALL/VMLAUNCH/VMRESUME/VMXOFF")
			1u   -> when (processor.cir.toUInt()) {
				0xC8u -> TODO("MONITOR")
				0xC9u -> TODO("MWAIT")
				else  -> return hex(rm.memRM.address.get()) +
						" [${hex(processor.idtrLimit.tx)}:${hex(processor.idtrBase.tex)}]"
			}
			2u   -> when (processor.cir.toUInt()) {
				0xD0u -> TODO("XGETBV")
				0xD1u -> TODO("XSETBV")
				else  -> {
					val addr = rm.memRM.address.get()
					return "${hex(processor.computer.requestMemoryAt16(addr))}/" +
							"${hex(processor.computer.requestMemoryAt32(addr + 2u))} " +
							"[${hex(rm.memRM.address.get())}]"
				}
			}
			3u   -> {
				val addr = rm.memRM.address.get()
				return "${hex(processor.computer.requestMemoryAt16(addr))}/" +
						"${hex(processor.computer.requestMemoryAt32(addr + 2u))} " +
						"[${hex(rm.memRM.address.get())}]"
			}
			4u   -> TODO("SMSW")
			6u   -> TODO("LMSW")
			7u   -> TODO("INVLPG/RDTSCP")
			else -> throw IllegalStateException("/$r")
		}
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
				}
			}
			2u   -> when (processor.cir.toUInt()) {
				0xD0u -> TODO("XGETBV")
				0xD1u -> TODO("XSETBV")
				else  -> {
					val addr = rm.memRM.address.get()
					processor.gdtrLimit.tx = processor.computer.requestMemoryAt16(addr)
					processor.gdtrBase.tex = processor.computer.requestMemoryAt32(addr + 2u)
				}
			}
			3u   -> {
				val addr = rm.memRM.address.get()
				processor.idtrLimit.tx = processor.computer.requestMemoryAt16(addr)
				processor.idtrBase.tex = processor.computer.requestMemoryAt32(addr + 2u)
			}
			4u   -> TODO("SMSW")
			6u   -> TODO("LMSW")
			7u   -> TODO("INVLPG/RDTSCP")
			else -> throw IllegalStateException("/$r")
		}
	}

	override val supportsCodeSegmentOverride: Boolean = false
}