package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register

@IA32InstructionCluster
class PopIntoRegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `58+ r(w/d)` |
	 * Instruction: `POP r(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PopIntoRegister(val r32n: String, val r16n: String, val register: Register) : ZeroOperandInstruction {
		override fun getMnemonic(processor: IA32Processor): String = "pop"
		override fun getOperands(processor: IA32Processor): String = when (processor.operatingModeLocal) {
			AddressingLength.R32 -> "$r32n [${hex(processor.pop32())}]"
			AddressingLength.R16 -> "$r16n [${hex(processor.pop16())}]"
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}

		override fun handle(processor: IA32Processor) = when (processor.operatingModeLocal) {
			AddressingLength.R32 -> register.tex = processor.pop32()
			AddressingLength.R16 -> register.tx = processor.pop16()
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}
	}

	@IA32Instruction(0x58u)
	val a = PopIntoRegister("eax", "ax", processor.a)

	@IA32Instruction(0x59u)
	val c = PopIntoRegister("ecx", "cx", processor.c)

	@IA32Instruction(0x5Au)
	val d = PopIntoRegister("edx", "dx", processor.d)

	@IA32Instruction(0x5Bu)
	val b = PopIntoRegister("ebx", "bx", processor.b)

	@IA32Instruction(0x5Cu)
	val sp = PopIntoRegister("esp", "sp", processor.sp)

	@IA32Instruction(0x5Du)
	val bp = PopIntoRegister("ebp", "bp", processor.bp)

	@IA32Instruction(0x5Eu)
	val si = PopIntoRegister("esi", "si", processor.si)

	@IA32Instruction(0x5Fu)
	val di = PopIntoRegister("edi", "di", processor.di)
}