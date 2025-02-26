package org.bread_experts_group.breadmod.experimental.computer.io

interface IODevice {
	fun read(): UByte
	fun write(d: UByte)
}