package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import kotlin.reflect.jvm.jvmName

interface Instruction {
	fun getMnemonic(processor: IA32Processor): String =
		this::class.jvmName.substringAfter("Instruction").lowercase()

	fun getDisassembly(processor: IA32Processor): String {
		val savedIP = processor.ip.rx
		val savedSP = processor.sp.rx
		val operands = when (processor.operatingModeLocal) {
			DecodingUtil.AddressingLength.R8, DecodingUtil.AddressingLength.R16 -> ::getOperands16
			DecodingUtil.AddressingLength.R32                                   -> ::getOperands32
		}(processor)
		processor.ip.rx = savedIP
		processor.sp.rx = savedSP
		val mnemonic = getMnemonic(processor)
		processor.ip.rx = savedIP
		processor.sp.rx = savedSP
		return "0x${this::class.jvmName.substringAfter('H').substringBefore("Instruction")}" +
				" $mnemonic $operands"
	}

	fun getOperands16(processor: IA32Processor): String =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 16-bits disassembly.")

	fun getOperands32(processor: IA32Processor): String =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 32-bits disassembly.")

	fun getOperands64(processor: IA32Processor): String =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 64-bits disassembly.")

	fun prepare(processor: IA32Processor) {}

	fun handle16(processor: IA32Processor): Unit =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 16-bits.")

	fun handle32(processor: IA32Processor): Unit =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 32-bits.")

	fun handle64(processor: IA32Processor): Unit =
		throw UnsupportedOperationException("${this::class.jvmName} cannot support 64-bits.")

	val supportsCodeSegmentOverride: Boolean
}