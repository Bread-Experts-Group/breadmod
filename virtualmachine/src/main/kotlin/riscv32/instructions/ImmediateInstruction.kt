package breadmod.rnd.riscv32.instructions

typealias ImmediateOpcodes = Map<UInt, OpcodeMethod<ImmediateInstruction>>

class ImmediateInstruction(instruction: UInt): InstructionFormat(instruction) {
    constructor(
        opcode: UInt,
        rd    : UInt,
        funct3: UInt,
        rs1   : UInt,
        imm   : UInt
    ) : this(opcode or rd or funct3 or rs1 or imm)

    val rd    : UInt = instruction and 0x00000F80u shr 7
    val funct3: UInt = instruction and 0x00007000u shr 12
    val rs1   : UInt = instruction and 0x000F8000u shr 15
    val imm   : UInt = instruction and 0xFFF00000u shr 20

    companion object {
        val funct3List0010011: ImmediateOpcodes = mapOf(
            0x0u to { cpu, ir -> // ADD Immediate
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue + ir.imm.toInt()
            },
            0x1u to { cpu, ir -> // Shift Left
                when(ir.imm and 0xEFu) {
                    0x00u -> cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue shl ir.imm.toInt()
                    else -> throw IllegalStateException("Illegal funct7")
                }
            },
            0x2u to { cpu, ir -> TODO("Set Less Than Imm") },
            0x3u to { cpu, ir -> TODO("Set Less Than Imm (U)") },
            0x4u to { cpu, ir -> // XOR Immediate
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue xor ir.imm.toInt()
            },
            0x5u to { cpu, ir -> // Shift Right
                when(ir.imm and 0xEFu) {
                    // Logical
                    0x00u -> cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue ushr ir.imm.toInt()
                    // Arithmetic
                    0x20u -> cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue shr  ir.imm.toInt()
                    else -> throw IllegalStateException("Illegal funct7")
                }
            },
            0x6u to { cpu, ir -> // OR Immediate
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue or ir.imm.toInt()
            },
            0x7u to { cpu, ir -> // AND Immediate
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rs1.toInt()].storedValue and ir.imm.toInt()
            },
        )
        val funct3List0000011: ImmediateOpcodes = mapOf(
            0x0u to { cpu, ir -> // Load Byte, DIRTY!
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rd.toInt()].storedValue and (cpu.readMemory<Byte>(cpu.registers[ir.rs1.toInt() + ir.imm.toInt()].storedValue).toInt())
            },
            0x1u to { cpu, ir -> // Load Half (Short), DIRTY!
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rd.toInt()].storedValue and (cpu.readMemory<Short>(cpu.registers[ir.rs1.toInt() + ir.imm.toInt()].storedValue).toInt())
            },
            0x2u to { cpu, ir -> // Load Word (Int), DIRTY!
                cpu.registers[ir.rd.toInt()].storedValue = cpu.registers[ir.rd.toInt()].storedValue and cpu.readMemory<Int>(cpu.registers[ir.rs1.toInt() + ir.imm.toInt()].storedValue)
            },
            0x4u to { cpu, ir -> // Load Half (Short) (U), DIRTY!
                cpu.registers[ir.rd.toInt()].storedValue = (cpu.readMemory<Short>(cpu.registers[ir.rs1.toInt() + ir.imm.toInt()].storedValue).toInt())
            },
            0x5u to { cpu, ir -> // Load Word (Int) (U), DIRTY!
                cpu.registers[ir.rd.toInt()].storedValue = cpu.readMemory<Int>(cpu.registers[ir.rs1.toInt() + ir.imm.toInt()].storedValue)
            },
        )
    }
}