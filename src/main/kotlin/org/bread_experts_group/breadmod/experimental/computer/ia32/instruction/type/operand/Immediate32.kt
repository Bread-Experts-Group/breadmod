package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface Immediate32 {
	fun IA32Processor.imm32(): UInt = this.decoding.readBinaryFetch(4).toUInt()
	fun IA32Processor.rel32(): Int = this.decoding.readBinaryFetch(4).toInt()
}