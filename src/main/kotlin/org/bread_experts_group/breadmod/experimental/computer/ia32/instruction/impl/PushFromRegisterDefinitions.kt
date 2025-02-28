package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister

@IA32InstructionCluster
class PushFromRegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `50+/9C r(w/d)` |
	 * Instruction: `PUSH r(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PushFromRegister(val r32n: String, val r16n: String, val register: Register) : ZeroOperandInstruction {
		override fun getMnemonic(processor: IA32Processor): String = "push"
		override fun getOperands(processor: IA32Processor): String = when (processor.operatingModeLocal) {
			AddressingLength.R32 -> "${this.r32n} [${hex(this.register.tex)}]"
			AddressingLength.R16 -> "${this.r16n} [${hex(this.register.tx)}]"
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operatingModeLocal) {
			AddressingLength.R32 -> processor.push32(this.register.tex)
			AddressingLength.R16 -> processor.push16(this.register.tx)
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}
	}

	@IA32Instruction(0x50u)
	val a: PushFromRegister = PushFromRegister("eax", "ax", processor.a)

	@IA32Instruction(0x51u)
	val c: PushFromRegister = PushFromRegister("ecx", "cx", processor.c)

	@IA32Instruction(0x52u)
	val d: PushFromRegister = PushFromRegister("edx", "dx", processor.d)

	@IA32Instruction(0x53u)
	val b: PushFromRegister = PushFromRegister("ebx", "bx", processor.b)

	@IA32Instruction(0x54u)
	val sp: PushFromRegister = PushFromRegister("esp", "sp", processor.sp)

	@IA32Instruction(0x55u)
	val bp: PushFromRegister = PushFromRegister("ebp", "bp", processor.bp)

	@IA32Instruction(0x56u)
	val si: PushFromRegister = PushFromRegister("esi", "si", processor.si)

	@IA32Instruction(0x57u)
	val di: PushFromRegister = PushFromRegister("edi", "di", processor.di)

	@IA32Instruction(0x9Cu)
	val flags: PushFromRegister = PushFromRegister("eflags", "flags", processor.flags)

	/**
	 * Opcode: `0E/16/1E/06/0FA0/0FA8` |
	 * Instruction: `PUSH (C/S/D/E/F/G)S` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PushFromSegmentRegister(val n: Char, val register: SegmentRegister) : ZeroOperandInstruction {
		override fun getMnemonic(processor: IA32Processor): String = "push"
		override fun getOperands(processor: IA32Processor): String = "${this.n}s [${hex(this.register.tx)}]"
		override fun handle(processor: IA32Processor): Unit = processor.push16(this.register.tx)
	}

	@IA32Instruction(0x0Eu)
	val cs: PushFromSegmentRegister = PushFromSegmentRegister('c', processor.cs)

	@IA32Instruction(0x16u)
	val ss: PushFromSegmentRegister = PushFromSegmentRegister('s', processor.ss)

	@IA32Instruction(0x1Eu)
	val ds: PushFromSegmentRegister = PushFromSegmentRegister('d', processor.ds)

	@IA32Instruction(0x06u)
	val es: PushFromSegmentRegister = PushFromSegmentRegister('e', processor.es)

	@IA32Instruction(0x0FA0u)
	val fs: PushFromSegmentRegister = PushFromSegmentRegister('f', processor.fs)

	@IA32Instruction(0x0FA8u)
	val gs: PushFromSegmentRegister = PushFromSegmentRegister('g', processor.gs)
}