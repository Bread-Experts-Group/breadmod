package breadmod.rnd.util

import java.io.EOFException
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteOrder

class OrderableRandomAccessFile(file: File, mode: String): RandomAccessFile(file, mode) {
    var order: ByteOrder = ByteOrder.BIG_ENDIAN
    fun readShortOrdered(): Short =
        if(order == ByteOrder.BIG_ENDIAN) readShort()
        else {
            val ch2 = this.read()
            val ch1 = this.read()
            if ((ch1 or ch2) < 0) throw EOFException()
            ((ch1 shl 8) + (ch2 shl 0)).toShort()
        }
    fun readIntOrdered(): Int =
        if(order == ByteOrder.BIG_ENDIAN) readInt()
        else {
            val ch4 = this.read()
            val ch3 = this.read()
            val ch2 = this.read()
            val ch1 = this.read()
            if ((ch1 or ch2 or ch3 or ch4) < 0) throw EOFException()
            ((ch1 shl 24) + (ch2 shl 16) + (ch3 shl 8) + (ch4 shl 0))
        }
    fun readLongOrdered(): Long =
        if(order == ByteOrder.BIG_ENDIAN) readLong()
        else (readIntOrdered().toLong() shl 32) + (readIntOrdered().toLong() and 0xFFFFFFFFL)
}