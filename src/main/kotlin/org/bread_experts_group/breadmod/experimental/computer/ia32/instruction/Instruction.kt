package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface Instruction {
	fun handle(processor: IA32Processor)
	fun getMnemonic(processor: IA32Processor): String
	fun getOperands(processor: IA32Processor): String
	fun getDisassembly(processor: IA32Processor): String {
		val savedIP = processor.ip.rx
		val savedSP = processor.sp.rx
		val mnemonic = getMnemonic(processor)
		processor.ip.rx = savedIP
		processor.sp.rx = savedSP
		val operands = getOperands(processor)
		processor.ip.rx = savedIP
		processor.sp.rx = savedSP
		return "$mnemonic $operands"
	}
}