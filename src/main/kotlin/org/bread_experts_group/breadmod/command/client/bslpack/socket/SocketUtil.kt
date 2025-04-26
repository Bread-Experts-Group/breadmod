package org.bread_experts_group.breadmod.command.client.bslpack.socket

import java.io.InputStream
import java.io.OutputStream
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

fun InputStream.read16() = (this.read() shl 8) or this.read()
fun InputStream.read16u() = this.read16().toUShort()
fun InputStream.read16ui() = this.read16u().toInt()

fun InputStream.read24() = (this.read16() shl 8) or this.read()
fun InputStream.read24u() = this.read24().toUInt()
fun InputStream.read24ui() = this.read24u().toInt()

fun InputStream.read32() = (this.read24() shl 8) or this.read()
fun InputStream.read32u() = this.read32().toUInt()
fun InputStream.read32ul() = this.read32u().toLong()

fun InputStream.read64() = (this.read32() shl 32).toLong() or this.read32().toLong()
fun InputStream.read64u() = this.read64().toULong()

fun InputStream.readInet4(): Inet4Address = Inet4Address.getByAddress(this.readNBytes(4)) as Inet4Address
fun InputStream.readInet6(): Inet6Address = Inet6Address.getByAddress(this.readNBytes(16)) as Inet6Address
fun InputStream.readString(n: Int): String = this.readNBytes(n).decodeToString()

fun OutputStream.write16(data: Int) = this.write(data shr 8).also { this.write(data) }
fun OutputStream.write24(data: Int) = this.write(data shr 16).also { this.write16(data) }
fun OutputStream.write32(data: Int) = this.write(data shr 24).also { this.write24(data) }
fun OutputStream.write32(data: Long) = this.write32(data.toInt())
fun OutputStream.write64(data: Long) = this.write32((data shr 32).toInt()).also { this.write32((data).toInt()) }
fun OutputStream.writeInet(data: InetAddress) = data.address.forEach { this.write(it.toInt()) }
fun OutputStream.writeString(s: String) = this.write(s.encodeToByteArray())

fun InputStream.scanDelimiter(lookFor: String): String {
	var bucket = ""
	var pool = ""
	while (bucket.length != lookFor.length) {
		val charCode = this.read()
		if (charCode == -1) break
		val next = Char(charCode)
		if (lookFor[bucket.length] == next) bucket += next
		else {
			pool += bucket + next
			bucket = ""
		}
	}
	return pool
}