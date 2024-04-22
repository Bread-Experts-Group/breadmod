package breadmod.rnd

import breadmod.rnd.util.ELFFile
import java.io.DataInputStream
import java.io.RandomAccessFile
import java.net.URL
import java.nio.file.Files
import java.util.*

fun main() {
    println("Begin ELF")
    val tempFile = RandomAccessFile(Files.createTempFile("elf",null).toFile(), "r")
    println(ELFFile.decodeElf(RandomAccessFile(URL("https://cdn.discordapp.com/attachments/936081172885291038/1231782758825857065/boot.elf?ex=66271297&is=6625c117&hm=8626dee01544106562ad5928a5515974c915e33e666b313e91016b7cd89315b1&").openStream())))
}
