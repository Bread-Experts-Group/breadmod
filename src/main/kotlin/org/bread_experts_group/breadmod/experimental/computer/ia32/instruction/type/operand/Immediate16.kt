package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

interface Immediate16 {
	fun IA32Processor.imm16(): UShort = this.decoding.readBinaryFetch(2).toUShort()
	fun IA32Processor.rel16(): Short = this.decoding.readBinaryFetch(2).toShort()
}