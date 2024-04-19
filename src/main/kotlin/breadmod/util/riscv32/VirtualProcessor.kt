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
        Register(0, RegisterSaver.NONE), // zero
        Register(saver = RegisterSaver.CALLER), // return address (ra)
        Register(saver = RegisterSaver.CALLEE), // stack pointer (sp)
        Register(saver = RegisterSaver.NONE), // global pointer (gp)
        Register(saver = RegisterSaver.NONE), // thread pointer (tp)
        Register(saver = RegisterSaver.CALLER), // temporary/alternate return (t0)
        Register(saver = RegisterSaver.CALLER), // temporary (t1)
        Register(saver = RegisterSaver.CALLER), // temporary (t2)
        Register(saver = RegisterSaver.CALLEE), // saved register/frame pointer (s0/fp)
        Register(saver = RegisterSaver.CALLEE), // saved register (s1)
        Register(saver = RegisterSaver.CALLER), // function arguments/return values (a0)
        Register(saver = RegisterSaver.CALLER), // ditto (a1)
        Register(saver = RegisterSaver.CALLER), // function arguments (a2)
        Register(saver = RegisterSaver.CALLER), // ditto (a3)
        Register(saver = RegisterSaver.CALLER), // ditto (a4)
        Register(saver = RegisterSaver.CALLER), // ditto (a5)
        Register(saver = RegisterSaver.CALLER), // ditto (a6)
        Register(saver = RegisterSaver.CALLER), // ditto (a7)
        Register(saver = RegisterSaver.CALLEE), // saved register (s2)
        Register(saver = RegisterSaver.CALLEE), // ditto (s3)
        Register(saver = RegisterSaver.CALLEE), // ditto (s4)
        Register(saver = RegisterSaver.CALLEE), // ditto (s5)
        Register(saver = RegisterSaver.CALLEE), // ditto (s6)
        Register(saver = RegisterSaver.CALLEE), // ditto (s7)
        Register(saver = RegisterSaver.CALLEE), // ditto (s8)
        Register(saver = RegisterSaver.CALLEE), // ditto (s9)
        Register(saver = RegisterSaver.CALLEE), // ditto (s10)
        Register(saver = RegisterSaver.CALLEE), // ditto (s11)
        Register(saver = RegisterSaver.CALLER), // temporary (t3)
        Register(saver = RegisterSaver.CALLER), // ditto (t4)
        Register(saver = RegisterSaver.CALLER), // ditto (t5)
        Register(saver = RegisterSaver.CALLER), // ditto (t6)
    )

    fun decodeInstruction(instruction: Int) {

    }

    abstract class InstructionFormat(open val instruction: Int) {
        abstract fun process(forCpu: VirtualProcessor)
    }

    /**
     * R-type [instruction format](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf#section.2.2)
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    class RegisterRegisterInstruction(override val instruction: Int): InstructionFormat(instruction) {
        constructor(
            opcode: Int,
            destinationRegister1: Int,
            opcodeExtension3B: Int,
            sourceRegister1: Int,
            sourceRegister2: Int,
            opcodeExtension7B: Int
        ) : this(
            opcode or destinationRegister1 or opcodeExtension3B or
            sourceRegister1 or sourceRegister2 or opcodeExtension7B
        )

        val opcode: Int = instruction and 0xFFFFFF
        val destinationRegister1: Int = (instruction and 0xFFFFF) shr 7 // one bit remaining
        val opcodeExtension3B: Int = 0
        val sourceRegister1: Int = 0
        val sourceRegister2: Int = 0
        val opcodeExtension7B: Int = 0

        override fun process(forCpu: VirtualProcessor) {
            TODO("Not yet implemented")
        }
    }
}