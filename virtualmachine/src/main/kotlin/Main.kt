package breadmod.rnd

import breadmod.rnd.riscv32.VirtualProcessor
import breadmod.rnd.util.ELFFile
import breadmod.rnd.util.OrderableRandomAccessFile
import java.nio.file.Files

fun main() {
    val tempFile = Files.createTempFile("elf",null).toFile()
    println("rig for silent run")
    tempFile::class.java.getResourceAsStream("riscv32.o").let {
        if(it != null) {
            it.copyTo(tempFile.outputStream())
            val oraf = OrderableRandomAccessFile(tempFile, "r")
            val elf = ELFFile.decodeElf(oraf)

            println("Begin scan for .text")
            val progbits = elf.sectionHeaders[".text"] ?: throw IllegalStateException("No .text!")
            oraf.seek(progbits.offset)
            val cpu = VirtualProcessor(Baseboard(memoryModules = listOf(Memory(8196))))
            cpu.executeProgram(oraf)
            it.close()
        }
    }
}