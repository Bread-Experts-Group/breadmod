package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.hD0

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySelectorSingleOperandGroupInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0xD0u)
class Selector(processor: IA32Processor) : RegisterMemorySelectorSingleOperandGroupInstruction {
	override val instructions: Map<UInt, RegisterMemorySingleOperandInstruction> = mapOf(
		4u to ShiftModRM8LeftOnce
	)
}