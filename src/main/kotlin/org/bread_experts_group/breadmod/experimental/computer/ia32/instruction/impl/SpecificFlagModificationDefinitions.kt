package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType

@IA32InstructionCluster
class SpecificFlagModificationDefinitions(processor: IA32Processor) {
	class SpecificFlagModification(n: Char, val flag: FlagType, val state: Boolean) :
		Instruction("${if (state) "st" else "cl"}$n") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			processor.flags.setFlag(this.flag, this.state)
		}
	}

	@IA32Instruction(0xF8u)
	val cc: SpecificFlagModification = SpecificFlagModification('c', FlagType.CARRY_FLAG, false)

	@IA32Instruction(0xF9u)
	val sc: SpecificFlagModification = SpecificFlagModification('c', FlagType.CARRY_FLAG, true)

	@IA32Instruction(0xFAu)
	val ci: SpecificFlagModification = SpecificFlagModification('i', FlagType.INTERRUPT_ENABLE_FLAG, false)

	@IA32Instruction(0xFBu)
	val si: SpecificFlagModification = SpecificFlagModification('i', FlagType.INTERRUPT_ENABLE_FLAG, true)

	@IA32Instruction(0xFCu)
	val cd: SpecificFlagModification = SpecificFlagModification('d', FlagType.DIRECTION_FLAG, false)

	@IA32Instruction(0xFDu)
	val sd: SpecificFlagModification = SpecificFlagModification('d', FlagType.DIRECTION_FLAG, true)
}