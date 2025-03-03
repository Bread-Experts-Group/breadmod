package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32InstructionCluster
class JumpOnConditionDefinitions8(processor: IA32Processor) {
	class JumpOnConditionImmediate8Displacement(
		name: String,
		val condition: ((FlagType) -> Boolean) -> Boolean
	) : Instruction("j$name"), Immediate8 {
		override fun operands(processor: IA32Processor): String = processor.rel8().let {
			"${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]"
		}

		override fun handle(processor: IA32Processor) {
			val rel8 = processor.rel8()
			if (this.condition(processor.flags::getFlag))
				processor.ip.tex = (processor.ip.tex.toInt() + rel8).toUInt()
		}
	}

	@IA32Instruction(0x70u)
	val jo: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("o") {
		it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x71u)
	val jno: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("no") {
		!it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x72u)
	val jc: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("c") {
		it(FlagType.CARRY_FLAG)
	}

	@IA32Instruction(0x73u)
	val jnc: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("nc") {
		!it(FlagType.CARRY_FLAG)
	}

	@IA32Instruction(0x74u)
	val je: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("e") {
		it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x75u)
	val jne: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("ne") {
		!it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x76u)
	val jbe: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("be") {
		it(FlagType.CARRY_FLAG) || it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x77u)
	val ja: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("a") {
		!it(FlagType.CARRY_FLAG) && !it(FlagType.ZERO_FLAG)
	}

	@IA32Instruction(0x78u)
	val js: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("s") {
		it(FlagType.SIGN_FLAG)
	}

	@IA32Instruction(0x79u)
	val jns: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("s") {
		!it(FlagType.SIGN_FLAG)
	}

	@IA32Instruction(0x7Au)
	val jp: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("p") {
		it(FlagType.PARITY_FLAG)
	}

	@IA32Instruction(0x7Bu)
	val jnp: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("np") {
		!it(FlagType.PARITY_FLAG)
	}

	@IA32Instruction(0x7Cu)
	val jl: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("l") {
		it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x7Du)
	val jge: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("ge") {
		it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG)
	}

	@IA32Instruction(0x7Eu)
	val jle: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("le") {
		it(FlagType.ZERO_FLAG) || (it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG))
	}

	@IA32Instruction(0x7Fu)
	val jg: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("g") {
		!it(FlagType.ZERO_FLAG) && (it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG))
	}

	@IA32Instruction(0xEBu)
	val jmp: JumpOnConditionImmediate8Displacement = JumpOnConditionImmediate8Displacement("mp") { true }
}