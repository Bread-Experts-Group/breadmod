package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import kotlin.reflect.KMutableProperty0

@IA32InstructionCluster
class MoveImmediateIntoRegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `B8+ r(w/d) i(w/d)` |
	 * Instruction: `MOV r(16/32), imm(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class MoveImmediateIntoRegister(val r32n: String, val r16n: String, val register: Register) :
		Instruction("mov"), Immediate32, Immediate16 {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "${this.r32n}, ${hex(processor.imm32())}"
			AddressingLength.R16 -> "${this.r16n}, ${hex(processor.imm16())}"
			else                 -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> this.register.tex = processor.imm32()
			AddressingLength.R16 -> this.register.tx = processor.imm16()
			else                 -> throw UnsupportedOperationException()
		}
	}

	@IA32Instruction(0xB8u)
	val a: MoveImmediateIntoRegister = MoveImmediateIntoRegister("eax", "ax", processor.a)

	@IA32Instruction(0xB9u)
	val c: MoveImmediateIntoRegister = MoveImmediateIntoRegister("ecx", "cx", processor.c)

	@IA32Instruction(0xBAu)
	val d: MoveImmediateIntoRegister = MoveImmediateIntoRegister("edx", "dx", processor.d)

	@IA32Instruction(0xBBu)
	val b: MoveImmediateIntoRegister = MoveImmediateIntoRegister("ebx", "bx", processor.b)

	@IA32Instruction(0xBCu)
	val sp: MoveImmediateIntoRegister = MoveImmediateIntoRegister("esp", "sp", processor.sp)

	@IA32Instruction(0xBDu)
	val bp: MoveImmediateIntoRegister = MoveImmediateIntoRegister("ebp", "bp", processor.bp)

	@IA32Instruction(0xBEu)
	val si: MoveImmediateIntoRegister = MoveImmediateIntoRegister("esi", "si", processor.si)

	@IA32Instruction(0xBFu)
	val di: MoveImmediateIntoRegister = MoveImmediateIntoRegister("edi", "di", processor.di)

	/**
	 * Opcode: `B0+ rb ib` |
	 * Instruction: `MOV r8, imm8` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class MoveImmediateIntoRegister8(val r8n: String, val register: KMutableProperty0<ULong>) :
		Instruction("mov"), Immediate8 {
		override fun operands(processor: IA32Processor): String = "${this.r8n}, ${hex(processor.imm8())}"
		override fun handle(processor: IA32Processor) {
			this.register.set(processor.imm8().toULong())
		}
	}

	@IA32Instruction(0xB0u)
	val al: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("al", processor.a::l)

	@IA32Instruction(0xB1u)
	val cl: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("cl", processor.c::l)

	@IA32Instruction(0xB2u)
	val dl: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("dl", processor.d::l)

	@IA32Instruction(0xB3u)
	val bl: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("bl", processor.b::l)

	@IA32Instruction(0xB4u)
	val ah: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("ah", processor.a::h)

	@IA32Instruction(0xB5u)
	val ch: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("ch", processor.c::h)

	@IA32Instruction(0xB6u)
	val dh: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("dh", processor.d::h)

	@IA32Instruction(0xB7u)
	val bh: MoveImmediateIntoRegister8 = MoveImmediateIntoRegister8("bh", processor.b::h)
}