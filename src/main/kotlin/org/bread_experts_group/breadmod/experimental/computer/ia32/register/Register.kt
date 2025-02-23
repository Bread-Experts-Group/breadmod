package org.bread_experts_group.breadmod.experimental.computer.ia32.register

import org.apache.logging.log4j.Logger

open class Register(val logger: Logger, val name: String, prx: ULong) {
	open var rx: ULong = prx
		set(value) {
//			if (name != "ip") this.logger.warn("$name set ${hex(value)}")
			field = value
		}
	var ex: ULong
		get() = this.rx and 0x00000000FFFFFFFFu
		set(value) {
			this.rx = (this.rx and 0xFFFFFFFF00000000u) or (value and 0x00000000FFFFFFFFu)
		}
	var tex: UInt
		get() = this.ex.toUInt()
		set(value) {
			this.ex = value.toULong()
		}
	var x: ULong
		get() = this.rx and 0x000000000000FFFFu
		set(value) {
			this.rx = (this.rx and 0xFFFFFFFFFFFF0000u) or (value and 0x000000000000FFFFu)
		}
	var tx: UShort
		get() = this.x.toUShort()
		set(value) {
			this.x = value.toULong()
		}
	var l: ULong
		get() = this.rx and 0x00000000000000FFu
		set(value) {
			this.rx = (this.rx and 0xFFFFFFFFFFFFFF00u) or (value and 0x00000000000000FFu)
		}
	var tl: UByte
		get() = this.l.toUByte()
		set(value) {
			this.l = value.toULong()
		}
	var h: ULong
		get() = (this.rx and 0xFF00u) shr 8
		set(value) {
			this.rx = (this.rx and 0xFFFFFFFFFFFF00FFu) or ((value and 0x00000000000000FFu) shl 8)
		}
	var th: UByte
		get() = this.h.toUByte()
		set(value) {
			this.h = value.toULong()
		}
}