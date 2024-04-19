package breadmod.util.riscv32

/**
 * RISC-V (32-bit) implementation
 *
 * TODO: Move to ext. project when available
 *
 * TODO: Implement embedded, 64-bit, 128-bit variants
 * @author Miko Elbrecht
 * @see Register
 * @since 1.0.0
 */
class VirtualProcessor {
    val registers = listOf(
        Register(0), // zero
        Register(), // return address (ra)
        Register(), // stack pointer (sp)
        Register(), // global pointer (gp)
        Register(), // thread pointer (tp)
        Register(), // temporary/alternate return (t0)
        Register(), // temporary (t1)
        Register(), // temporary (t2)
        Register(), // saved register/frame pointer (s0/fp)
        Register(), // saved register (s1)
        Register(), // function arguments/return values (a0)
        Register(), // ditto (a1)
        Register(), // function arguments (a2)
        Register(), // ditto (a3)
        Register(), // ditto (a4)
        Register(), // ditto (a5)
        Register(), // ditto (a6)
        Register(), // ditto (a7)
        Register(), // saved register (s2)
        Register(), // ditto (s3)
        Register(), // ditto (s4)
        Register(), // ditto (s5)
        Register(), // ditto (s6)
        Register(), // ditto (s7)
        Register(), // ditto (s8)
        Register(), // ditto (s9)
        Register(), // ditto (s10)
        Register(), // ditto (s11)
        Register(), // temporary (t3)
        Register(), // ditto (t4)
        Register(), // ditto (t5)
        Register(), // ditto (t6)
    )

    fun decodeInstruction(instruction: Int) {
        val opcode = instruction and 0xFFFFFF
    }
}