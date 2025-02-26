package org.bread_experts_group.breadmod.experimental.computer.io

class Diagnostics : IODevice {
	val read = mutableListOf<UByte>()
	override fun read(): UByte = 0u
	override fun write(d: UByte) {
		read.add(d)
	}
}