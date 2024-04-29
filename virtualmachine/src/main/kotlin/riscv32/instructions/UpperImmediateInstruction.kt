package breadmod.rnd.riscv32.instructions

import breadmod.rnd.riscv32.VirtualProcessor

/**
 * R-type [instruction format](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf#section.2.2)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class UpperImmediateInstruction(instruction: UInt): InstructionFormat(instruction) {
    constructor(
        opcode: UInt,
        rd    : UInt,
        imm   : UInt
    ) : this(opcode or rd or imm)

    val rd : UInt = instruction and 0x00000F80u shr 7
    val imm: UInt = instruction and 0xFFFFF000u shr 12

    companion object {
        fun auipic(cpu: VirtualProcessor, upperImmediateInstruction: UpperImmediateInstruction) {

        }
    }
}