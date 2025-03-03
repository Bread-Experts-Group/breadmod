package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface Immediate8 {
	fun IA32Processor.imm8(): UByte = this.decoding.readFetch()
	fun IA32Processor.rel8(): Byte = this.decoding.readFetch().toByte()
}