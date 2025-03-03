package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register

@IA32InstructionCluster
class ExchangeAWithRegisterDefinitions(processor: IA32Processor) {
	class ExchangeAWithRegister(val r32n: String, val r16n: String, val register: Register) : Instruction("xchg") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "eax, ${this.r32n}"
			AddressingLength.R16 -> "ax, ${this.r16n}"
			else                 -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> {
				val temp = processor.a.tex
				processor.a.tex = this.register.tex
				this.register.tex = temp
			}
			AddressingLength.R16 -> {
				val temp = processor.a.tx
				processor.a.tx = this.register.tx
				this.register.tx = temp
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	@IA32Instruction(0x90u)
	val a: ExchangeAWithRegister = ExchangeAWithRegister("eax", "ax", processor.a)

	@IA32Instruction(0x91u)
	val c: ExchangeAWithRegister = ExchangeAWithRegister("ecx", "cx", processor.c)

	@IA32Instruction(0x92u)
	val d: ExchangeAWithRegister = ExchangeAWithRegister("edx", "dx", processor.d)

	@IA32Instruction(0x93u)
	val b: ExchangeAWithRegister = ExchangeAWithRegister("ebx", "bx", processor.b)

	@IA32Instruction(0x94u)
	val sp: ExchangeAWithRegister = ExchangeAWithRegister("esp", "sp", processor.sp)

	@IA32Instruction(0x95u)
	val bp: ExchangeAWithRegister = ExchangeAWithRegister("ebp", "bp", processor.bp)

	@IA32Instruction(0x96u)
	val si: ExchangeAWithRegister = ExchangeAWithRegister("esi", "si", processor.si)

	@IA32Instruction(0x97u)
	val di: ExchangeAWithRegister = ExchangeAWithRegister("edi", "di", processor.di)
}