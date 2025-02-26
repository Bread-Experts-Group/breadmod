package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register

@IA32InstructionCluster
class IncrementRegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `40+ r(w/d)` |
	 * Instruction: `INC r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class IncrementRegister(val r32n: String, val r16n: String, val register: Register) : ZeroOperandInstruction,
		ArithmeticAdditionFlagOperations {
		override fun getMnemonic(processor: IA32Processor): String = "inc"
		override fun getOperands(processor: IA32Processor): String = when (processor.operatingModeLocal) {
			AddressingLength.R32 -> "${this.r32n} [${hex(this.register.tex + 1u)}]"
			AddressingLength.R16 -> "${this.r16n} [${hex(this.register.tx + 1u)}]"
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operatingModeLocal) {
			AddressingLength.R32 -> {
				this.setFlagsForOperation(processor, this.register.ex, 1u)
				this.register.tex += 1u
				this.setFlagsForResult(processor, this.register.tex)
			}
			AddressingLength.R16 -> {
				this.setFlagsForOperation(processor, this.register.ex, 1u)
				this.register.x += 1u
				this.setFlagsForResult(processor, this.register.tex)
			}
			else                 -> throw IllegalArgumentException("Unsupported mode")
		}
	}

	@IA32Instruction(0x40u)
	val a: IncrementRegister = IncrementRegister("eax", "ax", processor.a)

	@IA32Instruction(0x41u)
	val c: IncrementRegister = IncrementRegister("ecx", "cx", processor.c)

	@IA32Instruction(0x42u)
	val d: IncrementRegister = IncrementRegister("edx", "dx", processor.d)

	@IA32Instruction(0x43u)
	val b: IncrementRegister = IncrementRegister("ebx", "bx", processor.b)

	@IA32Instruction(0x44u)
	val sp: IncrementRegister = IncrementRegister("esp", "sp", processor.sp)

	@IA32Instruction(0x45u)
	val bp: IncrementRegister = IncrementRegister("ebp", "bp", processor.bp)

	@IA32Instruction(0x46u)
	val si: IncrementRegister = IncrementRegister("esi", "si", processor.si)

	@IA32Instruction(0x47u)
	val di: IncrementRegister = IncrementRegister("edi", "di", processor.di)
}