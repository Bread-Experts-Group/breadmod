package breadmod.rnd.riscv32.instructions

import breadmod.rnd.riscv32.VirtualProcessor

typealias OpcodeMethod<T> = (VirtualProcessor, T) -> Unit

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "RedundantModalityModifier")
open class InstructionFormat(final val instruction: UInt) {
    val opcode: UInt = instruction and 0x0000007Fu

    companion object {
        fun findInstruction(forInstruction: UInt): InstructionFormat {
            val baseInstruction = InstructionFormat(forInstruction)
            return when(baseInstruction.opcode) {
                0b0110011u -> RegisterRegisterInstruction(forInstruction)
                0b0010011u, 0b0000011u, 0b1100111u -> ImmediateInstruction(forInstruction)
                0b0110111u, 0b0010111u -> UpperImmediateInstruction(forInstruction)
                0b0100011u -> StoreInstruction(forInstruction)
                else -> throw IllegalStateException("Illegal opcode ${baseInstruction.opcode.toString(radix = 2).padStart(7, '0')}")
            }
        }
    }
}