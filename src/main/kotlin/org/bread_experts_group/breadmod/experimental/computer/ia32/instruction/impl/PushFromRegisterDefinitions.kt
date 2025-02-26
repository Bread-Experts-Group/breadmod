package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register

@IA32InstructionCluster
class PushFromRegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `50+ r(w/d)` |
	 * Instruction: `PUSH r(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PushFromRegister(val r32n: String, val r16n: String, val register: Register) : Instruction {
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
}