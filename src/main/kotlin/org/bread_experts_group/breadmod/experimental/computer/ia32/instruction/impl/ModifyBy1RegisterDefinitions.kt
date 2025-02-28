package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticAdditionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register

@IA32InstructionCluster
class ModifyBy1RegisterDefinitions(processor: IA32Processor) {
	/**
	 * Opcode: `40+/48+ r(w/d)` |
	 * Instruction: `(IN/DE)C r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	abstract class ModifyRegister1(
		val r32n: String, val r16n: String, val register: Register,
		val s2: String, val op: (ULong, ULong) -> ULong
	) : ZeroOperandOperatingLengthDependentInstruction, ArithmeticFlagOperations {
		override fun getMnemonic(processor: IA32Processor): String = "${this.s2}c"
		override fun getOperands16(processor: IA32Processor): String = "${this.r16n} [${hex(this.register.tx + 1u)}]"
		override fun getOperands32(processor: IA32Processor): String = "${this.r32n} [${hex(this.register.tex + 1u)}]"
		override fun handle16(processor: IA32Processor) {
			this.setFlagsForLocalOp16(processor, this.register.x)
			this.register.tx = this.op(this.register.x, 1u).toUShort()
			this.setFlagsForResult(processor, this.register.tx)
		}

		override fun handle32(processor: IA32Processor) {
			this.setFlagsForLocalOp32(processor, this.register.ex)
			this.register.tex = this.op(this.register.ex, 1u).toUInt()
			this.setFlagsForResult(processor, this.register.tex)
		}

		abstract fun setFlagsForLocalOp16(processor: IA32Processor, r: ULong)
		abstract fun setFlagsForLocalOp32(processor: IA32Processor, r: ULong)
	}

	/**
	 * Opcode: `40+ r(w/d)` |
	 * Instruction: `INC r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class Increment(
		r32n: String, r16n: String, register: Register
	) : ModifyRegister1(r32n, r16n, register, "in", ULong::plus), ArithmeticAdditionFlagOperations {
		override fun setFlagsForLocalOp16(processor: IA32Processor, r: ULong) {
			this.setFlagsForOperationR(processor, r, (1u).toUShort())
		}

		override fun setFlagsForLocalOp32(processor: IA32Processor, r: ULong) {
			this.setFlagsForOperationR(processor, r, (1u).toUInt())
		}
	}

	@IA32Instruction(0x40u)
	val a: Increment = Increment("eax", "ax", processor.a)

	@IA32Instruction(0x41u)
	val c: Increment = Increment("ecx", "cx", processor.c)

	@IA32Instruction(0x42u)
	val d: Increment = Increment("edx", "dx", processor.d)

	@IA32Instruction(0x43u)
	val b: Increment = Increment("ebx", "bx", processor.b)

	@IA32Instruction(0x44u)
	val sp: Increment = Increment("esp", "sp", processor.sp)

	@IA32Instruction(0x45u)
	val bp: Increment = Increment("ebp", "bp", processor.bp)

	@IA32Instruction(0x46u)
	val si: Increment = Increment("esi", "si", processor.si)

	@IA32Instruction(0x47u)
	val di: Increment = Increment("edi", "di", processor.di)

	/**
	 * Opcode: `48+ r(w/d)` |
	 * Instruction: `DEC r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class Decrement(
		r32n: String, r16n: String, register: Register
	) : ModifyRegister1(r32n, r16n, register, "de", ULong::minus), ArithmeticSubtractionFlagOperations {
		override fun setFlagsForLocalOp16(processor: IA32Processor, r: ULong) {
			this.setFlagsForOperationR(processor, r, (1u).toUShort())
		}

		override fun setFlagsForLocalOp32(processor: IA32Processor, r: ULong) {
			this.setFlagsForOperationR(processor, r, (1u).toUInt())
		}
	}

	@IA32Instruction(0x48u)
	val sa: Decrement = Decrement("eax", "ax", processor.a)

	@IA32Instruction(0x49u)
	val sc: Decrement = Decrement("ecx", "cx", processor.c)

	@IA32Instruction(0x4Au)
	val sd: Decrement = Decrement("edx", "dx", processor.d)

	@IA32Instruction(0x4Bu)
	val sb: Decrement = Decrement("ebx", "bx", processor.b)

	@IA32Instruction(0x4Cu)
	val ssp: Decrement = Decrement("esp", "sp", processor.sp)

	@IA32Instruction(0x4Du)
	val sbp: Decrement = Decrement("ebp", "bp", processor.bp)

	@IA32Instruction(0x4Eu)
	val ssi: Decrement = Decrement("esi", "si", processor.si)

	@IA32Instruction(0x4Fu)
	val sdi: Decrement = Decrement("edi", "di", processor.di)
}