package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

object H0F01InstructionGROUP : Instruction {
	override fun prepare(processor: IA32Processor) {
		processor.fetch()
	}

	private fun operation(r: UInt): Nothing = when (r) {
		0u   -> TODO("SGDT/VMCALL/VMLAUNCH/VMRESUME/VMXOFF")
		1u   -> TODO("SIDT/MONITOR/MWAIT")
		2u   -> TODO("LGDT/XGETBV/XSETBV")
		3u   -> TODO("LIDT")
		4u   -> TODO("SMSW")
		6u   -> TODO("LMSW")
		7u   -> TODO("INVLPG/RDTSCP")
		else -> throw IllegalStateException("/$r")
	}

	override fun handle32(processor: IA32Processor) {
		val (_, r) = processor.decoding.getModRM(processor.cir)
		operation(r)
	}

	override val supportsCodeSegmentOverride: Boolean = false
}