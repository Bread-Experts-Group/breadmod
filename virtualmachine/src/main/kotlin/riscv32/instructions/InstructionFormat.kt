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
                else -> throw IllegalStateException("Illegal opcode")
            }
        }
    }
}