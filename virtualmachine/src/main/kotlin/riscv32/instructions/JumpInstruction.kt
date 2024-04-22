package breadmod.rnd.riscv32.instructions

class JumpInstruction(instruction: UInt): InstructionFormat(instruction) {
    constructor(
        opcode: UInt,
        rd    : UInt,
        imm1  : UInt,
        imm2  : UInt
    ) : this(opcode or rd or imm1 or imm2)

    val rd  : UInt = instruction and 0x00000F80u shr 7
    val imm1: UInt = instruction and 0x000FF000u shr 12
    val imm2: UInt = instruction and 0x7FE00000u shr 20
}