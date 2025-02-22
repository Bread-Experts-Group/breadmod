package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import kotlin.reflect.jvm.jvmName

interface Instruction {
	fun prepare(processor: IA32Processor) {}

	fun handle16(processor: IA32Processor): Unit =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 16-bits.")

	fun handle32(processor: IA32Processor): Unit =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 32-bits.")

	fun handle64(processor: IA32Processor): Unit =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 64-bits.")

	val supportsCodeSegmentOverride: Boolean
}