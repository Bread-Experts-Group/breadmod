package breadmod.rnd.riscv32.instructions

/**
 * R-type [instruction format](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf#section.2.2)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class BranchInstruction(instruction: UInt): InstructionFormat(instruction) {
    constructor(
        opcode: UInt,
        rd    : UInt,
        imm1  : UInt,
        rs1   : UInt,
        rs2   : UInt,
        imm2  : UInt
    ) : this(opcode or rd or imm1 or rs1 or rs2 or imm2)

    val rd  : UInt = instruction and 0x00000F80u shr 7
    val imm1: UInt = instruction and 0x00006000u shr 12
    val rs1 : UInt = instruction and 0x000F8000u shr 15
    val rs2 : UInt = instruction and 0x01F00000u shr 20
    val imm2: UInt = instruction and 0xEE000000u shr 25

    companion object {
        val funct3List = mapOf<UInt, OpcodeMethod<BranchInstruction>>(

        )
    }
}