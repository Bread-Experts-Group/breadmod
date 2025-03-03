package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister

@IA32InstructionCluster
class PopIntoRegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `58+/9D r(w/d)` |
	 * Instruction: `POP r(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PopIntoRegister(val r32n: String, val r16n: String, val register: Register) : Instruction("pop") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "${this.r32n} [${hex(processor.pop32())}]"
			AddressingLength.R16 -> "${this.r16n} [${hex(processor.pop16())}]"
			else                 -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> this.register.tex = processor.pop32()
			AddressingLength.R16 -> this.register.tx = processor.pop16()
			else                 -> throw UnsupportedOperationException()
		}
	}

	@IA32Instruction(0x58u)
	val a: PopIntoRegister = PopIntoRegister("eax", "ax", processor.a)

	@IA32Instruction(0x59u)
	val c: PopIntoRegister = PopIntoRegister("ecx", "cx", processor.c)

	@IA32Instruction(0x5Au)
	val d: PopIntoRegister = PopIntoRegister("edx", "dx", processor.d)

	@IA32Instruction(0x5Bu)
	val b: PopIntoRegister = PopIntoRegister("ebx", "bx", processor.b)

	@IA32Instruction(0x5Cu)
	val sp: PopIntoRegister = PopIntoRegister("esp", "sp", processor.sp)

	@IA32Instruction(0x5Du)
	val bp: PopIntoRegister = PopIntoRegister("ebp", "bp", processor.bp)

	@IA32Instruction(0x5Eu)
	val si: PopIntoRegister = PopIntoRegister("esi", "si", processor.si)

	@IA32Instruction(0x5Fu)
	val di: PopIntoRegister = PopIntoRegister("edi", "di", processor.di)

	@IA32Instruction(0x9Du)
	val flags: PopIntoRegister = PopIntoRegister("eflags", "flags", processor.flags)

	/**
	 * Opcode: `1F/07/17` |
	 * Instruction: `POP (D/E/S)S` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PopIntoSegmentRegister(val n: Char, val register: SegmentRegister) : Instruction("pop") {
		override fun operands(processor: IA32Processor): String = "${this.n}s [${hex(processor.pop16())}]"
		override fun handle(processor: IA32Processor) {
			this.register.tx = processor.pop16()
		}
	}

	@IA32Instruction(0x1Fu)
	val ds: PopIntoSegmentRegister = PopIntoSegmentRegister('d', processor.ds)

	@IA32Instruction(0x07u)
	val es: PopIntoSegmentRegister = PopIntoSegmentRegister('e', processor.es)

	@IA32Instruction(0x17u)
	val ss: PopIntoSegmentRegister = PopIntoSegmentRegister('s', processor.ss)
}