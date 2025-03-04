package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32InstructionCluster
class JumpOnConditionDefinitions(processor: IA32Processor) {
	class JumpOnConditionImmediateDisplacement(
		name: String,
		val condition: ((FlagType) -> Boolean) -> Boolean
	) : Instruction("j$name"), Immediate16, Immediate32 {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> processor.rel32()
				.let { "${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]" }
			AddressingLength.R16 -> processor.rel16()
				.let { "${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]" }
			else                 -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> {
				val rel32 = processor.rel32()
				if (this.condition(processor.flags::getFlag))
					processor.ip.tex = (processor.ip.tex.toInt() + rel32).toUInt()
				else {
				}
			}
			AddressingLength.R16 -> {
				val rel16 = processor.rel16()
				if (this.condition(processor.flags::getFlag))
					processor.ip.tex = (processor.ip.tex.toInt() + rel16).toUInt()
				else {
				}
			}
			else                 -> throw UnsupportedOperationException()
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