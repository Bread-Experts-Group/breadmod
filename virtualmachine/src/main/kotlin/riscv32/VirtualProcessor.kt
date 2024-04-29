package breadmod.rnd.riscv32

import breadmod.rnd.Baseboard
import breadmod.rnd.Memory
import breadmod.rnd.riscv32.instructions.*
import breadmod.rnd.util.OrderableRandomAccessFile
import kotlin.coroutines.coroutineContext

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

    fun executeProgram(data: OrderableRandomAccessFile) {
        repeat(82) {

            when(val instruction = InstructionFormat.findInstruction(data.readIntOrdered().toUInt().also { println(it.toString(radix = 2).padStart(32, '0')) })) {
                is RegisterRegisterInstruction -> RegisterRegisterInstruction.funct3List[instruction.funct3]?.get(instruction.funct7)!!.invoke(this, instruction)
                is ImmediateInstruction -> {
                    when(instruction.opcode) {
                        0b0000011u -> ImmediateInstruction.funct3List0000011[instruction.funct3]!!.invoke(this, instruction)
                        0b0010011u -> ImmediateInstruction.funct3List0010011[instruction.funct3]!!.invoke(this, instruction)
                        0b1100111u -> ImmediateInstruction.jalr(this, instruction)
                    }
                }
                is UpperImmediateInstruction -> {
                    when(instruction.opcode) {
                        0b0110111u -> TODO("")
                        0b0010111u -> UpperImmediateInstruction.auipic(this, instruction)
                    }
                }
                is StoreInstruction -> StoreInstruction.funct3List[instruction.funct3]!!.invoke(this, instruction)
                else -> throw IllegalStateException("Illegal operation")
            }
            println(this.parent.memoryModules)
            println("OUT < ${registers.joinToString { it.storedValue.toString() }}")
        }
    }

    fun getModuleAtAddress(offset: Long): Pair<Memory, Long> {
        var currentOffset = offset
        return parent.memoryModules.first {
            val size = it.data.size
            if(currentOffset <= size) true else { currentOffset -= size; false }
        } to currentOffset
    }

    fun combine(vararg bytes: Byte): Long {
        var concat = 0L
        bytes.forEach { concat = (concat shl 8) or it.toLong() }
        return concat
    }

    inline fun <reified T> readMemory(offset: Long): T {
        val location = getModuleAtAddress(offset)
        return when (T::class) {
            Int::class -> { combine(*location.first.data.slice(offset, 4).toByteArray()).toInt() as T }
            Byte::class -> { location.first.data[location.second] as T }
            else -> throw IllegalStateException("Illegal type")
        }
    }

    inline fun <reified T> writeMemory(offset: Long, data: T) {
        val location = getModuleAtAddress(offset)
        when (T::class) {
            Int::class -> {
                val int = data as Int
                location.first.data[location.second + 3] = (int shr 24).toByte()
                location.first.data[location.second + 2] = (int shr 16).toByte()
                location.first.data[location.second + 1] = (int shr 8 ).toByte()
                location.first.data[location.second    ] = (int       ).toByte()
            }
            Byte::class -> { location.first.data[location.second] = data as Byte }
            else -> throw IllegalStateException("Illegal type")
        }
    }

    inline fun <reified T> writeMemory(offset: Int, data: T) = writeMemory<T>(offset.toLong(), data)
    inline fun <reified T> readMemory(offset: Int): T = readMemory<T>(offset.toLong())
}
