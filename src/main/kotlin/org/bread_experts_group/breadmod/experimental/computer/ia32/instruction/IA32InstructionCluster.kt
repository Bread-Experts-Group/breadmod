package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

/**
 * Defines a cluster of [Instruction] fields annotated with [IA32Instruction], contained within a class with a
 * constructor of signature ([IA32Processor]).
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
annotation class IA32InstructionCluster