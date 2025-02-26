package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ImmediateSignedOperatingLengthSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32InstructionCluster
class JumpOnConditionDefinitions(processor: IA32Processor) {
	class JumpOnConditionImmediateDisplacement(
		val name: String,
		val condition: ((FlagType) -> Boolean) -> Boolean
	) : ImmediateSignedOperatingLengthSingleOperandInstruction {
		override fun getMnemonic(processor: IA32Processor): String = "j${this.name}"
		override fun getOperands16(processor: IA32Processor, rel16: Short): String =
			"${hex(rel16)} [${hex((processor.ip.tex.toInt() + rel16).toUInt())}]"

		override fun getOperands32(processor: IA32Processor, rel32: Int): String =
			"${hex(rel32)} [${hex((processor.ip.tex.toInt() + rel32).toUInt())}]"

		override fun handle16(processor: IA32Processor, rel16: Short) {
			if (this.condition(processor.flags::getFlag)) processor.ip.tex = (processor.ip.tex.toInt() + rel16).toUInt()
		}

		override fun handle32(processor: IA32Processor, rel32: Int) {
			if (this.condition(processor.flags::getFlag)) processor.ip.tex = (processor.ip.tex.toInt() + rel32).toUInt()
		}
	}

	@IA32Instruction(0x0F80u)
	val jo: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("o") {
		it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x0F81u)
	val jno: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("no") {
		!it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x0F82u)
	val jb: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("b") {
		it(FlagType.CARRY_FLAG)
	}

	@IA32Instruction(0x0F83u)
	val jnb: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("nb") {
		!it(FlagType.CARRY_FLAG)
	}

	@IA32Instruction(0x0F84u)
	val je: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("e") {
		it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x0F85u)
	val jne: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("ne") {
		!it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x0F86u)
	val jbe: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("be") {
		it(FlagType.CARRY_FLAG) || it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x0F87u)
	val jnbe: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("nbe") {
		!it(FlagType.CARRY_FLAG) && !it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x0F88u)
	val js: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("s") {
		it(FlagType.SIGN_FLAG)
	}

	@IA32Instruction(0x0F89u)
	val jns: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("ns") {
		!it(FlagType.SIGN_FLAG)
	}

	@IA32Instruction(0x0F8Au)
	val jp: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("p") {
		it(FlagType.PARITY_FLAG)
	}

	@IA32Instruction(0x0F8Bu)
	val jnp: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("np") {
		!it(FlagType.PARITY_FLAG)
	}

	@IA32Instruction(0x0F8Cu)
	val jl: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("l") {
		it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x0F8Du)
	val jnl: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("nl") {
		it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x0F8Eu)
	val jle: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("le") {
		it(FlagType.ZERO_FLAG) || (it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG))
	}

	@IA32Instruction(0x0F8Fu)
	val jnle: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("nle") {
		!it(FlagType.ZERO_FLAG) && (it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG))
	}

	@IA32Instruction(0xE9u)
	val jmp: JumpOnConditionImmediateDisplacement = JumpOnConditionImmediateDisplacement("mp") { true }
}