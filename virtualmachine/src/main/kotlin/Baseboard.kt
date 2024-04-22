package breadmod.rnd

import breadmod.rnd.riscv32.VirtualProcessor

class Baseboard(processorCount: Int = 1, val memoryModules: List<Memory>) {
    val processors = List(processorCount) { VirtualProcessor(this) }
}