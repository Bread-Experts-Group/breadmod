package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h83

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x83u)
class Selector(processor: IA32Processor) : InstructionSelector(processor) {
	override val instructions: Map<UInt, Instruction> = mapOf(
		0u to AddImmediate8ToModRM,
		1u to ORImmediate8ToModRM,
		2u to AddWithCarryImmediate8ToModRM,
//		3u to SubtractWithBorrowImmediate8FromModRM,
		4u to ANDImmediate8ToModRM,
		5u to SubtractImmediate8FromModRM,
//		7u to CompareImmediate8WithModRM
	)
}