package breadmod.rnd

import breadmod.rnd.util.BinaryStringCursor
import breadmod.rnd.util.ELFFile
import java.net.URL
import java.util.*

fun main() {
    val data = BinaryStringCursor(Scanner(URL("https://cdn.discordapp.com/attachments/936081172885291038/1231782758825857065/boot.elf?ex=66271297&is=6625c117&hm=8626dee01544106562ad5928a5515974c915e33e666b313e91016b7cd89315b1&").openStream(), "UTF-8").useDelimiter("\\A").next())
    println(ELFFile.decodeElf(data))
}