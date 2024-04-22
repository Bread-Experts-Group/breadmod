package breadmod.rnd.riscv32.instructions

typealias RROpcodes = Map<UInt, Map<UInt, OpcodeMethod<RegisterRegisterInstruction>>>

/**
 * R-type [instruction format](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf#section.2.2)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class RegisterRegisterInstruction(instruction: UInt): InstructionFormat(instruction) {
    constructor(
        opcode: UInt,
        rd    : UInt,
        funct3: UInt,
        rs1   : UInt,
        rs2   : UInt,
        funct7: UInt
    ) : this(opcode or rd or funct3 or rs1 or rs2 or funct7)

    val rd    : UInt = instruction and 0x00000F80u shr 7
    val funct3: UInt = instruction and 0x00007000u shr 12
    val rs1   : UInt = instruction and 0x000F8000u shr 15
    val rs2   : UInt = instruction and 0x01F00000u shr 20
    val funct7: UInt = instruction and 0xFE000000u shr 25

    companion object {
        val funct3List: RROpcodes = mapOf(
            0x0u to mapOf(
                0x00u to { cpu, rr -> // ADD
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue + cpu.registers[rr.rs2.toInt()].storedValue
                },
                0x20u to { cpu, rr -> // SUB
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue - cpu.registers[rr.rs2.toInt()].storedValue
                }
            ),
            0x1u to mapOf(
                0x00u to { cpu, rr -> // SHIFT LEFT
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue shl cpu.registers[rr.rs2.toInt()].storedValue
                }
            ),
            0x2u to mapOf(
                0x00u to { cpu, rr ->
                    TODO("Set less than")
                }
            ),
            0x3u to mapOf(
                0x00u to { cpu, rr ->
                    TODO("Set less than (u)")
                }
            ),
            0x4u to mapOf(
                0x00u to { cpu, rr -> // XOR
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue xor cpu.registers[rr.rs2.toInt()].storedValue
                }
            ),
            0x5u to mapOf(
                0x00u to { cpu, rr -> // SHIFT RIGHT
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue ushr cpu.registers[rr.rs2.toInt()].storedValue
                },
                0x20u to { cpu, rr -> // SHIFT RIGHT ARITHMETIC
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue shr cpu.registers[rr.rs2.toInt()].storedValue
                },
            ),
            0x6u to mapOf(
                0x00u to { cpu, rr -> // OR
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue or cpu.registers[rr.rs2.toInt()].storedValue
                }
            ),
            0x7u to mapOf(
                0x00u to { cpu, rr -> // AND
                    cpu.registers[rr.rd.toInt()].storedValue = cpu.registers[rr.rs1.toInt()].storedValue and cpu.registers[rr.rs2.toInt()].storedValue
                }
            ),
        )
    }
}