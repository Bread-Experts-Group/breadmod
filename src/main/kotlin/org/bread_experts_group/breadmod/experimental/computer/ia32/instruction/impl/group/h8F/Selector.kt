package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h8F

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemoryOperatingLengthDependentSingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySelectorSingleOperandGroupInstruction

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x8Fu)
class Selector(processor: IA32Processor) : RegisterMemorySelectorSingleOperandGroupInstruction {
	override val instructions: Map<UInt, RegisterMemoryOperatingLengthDependentSingleOperandInstruction> = mapOf(
		0u to PopIntoModRM
	)
}