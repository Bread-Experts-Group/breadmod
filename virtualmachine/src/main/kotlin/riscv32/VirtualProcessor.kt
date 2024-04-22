package breadmod.rnd.riscv32

import breadmod.rnd.Baseboard
import breadmod.rnd.Memory
import breadmod.rnd.riscv32.instructions.InstructionFormat
import breadmod.rnd.riscv32.instructions.RegisterRegisterInstruction

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
class VirtualProcessor(val parent: Baseboard) {
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

    @OptIn(ExperimentalStdlibApi::class)
    fun executeProgram(string: String) {
        string.split(Regex("........")).forEach {
            when(val instruction = InstructionFormat.findInstruction(it.hexToUInt())) {
                is RegisterRegisterInstruction -> RegisterRegisterInstruction.funct3List[instruction.funct3]?.get(instruction.funct7)?.invoke(this, instruction)
            }
        }
    }

    fun getModuleAtAddress(offset: Long): Pair<Memory, Long> {
        var currentOffset = offset
        return parent.memoryModules.first {
            val size = it.data.size
            if(size == null || currentOffset <= size) true else { currentOffset -= size; false }
        } to currentOffset
    }

    inline fun <reified T> readMemory(offset: Long): T {
        val location = getModuleAtAddress(offset)
        return when (T::class) {
            Int::class -> location.first.data[location.second]?.toInt() as T
            else -> throw IllegalStateException("Illegal type")
        }
    }

    inline fun <reified T> readMemory(offset: Int): T = readMemory<T>(offset.toLong())
}
