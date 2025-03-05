package org.bread_experts_group.breadmod.experimental.computer.io

class BreadModPollingVirtualKeyboard : IODevice {
	private val waiter: Object = Object()
	private var char: UByte = 0u

	override fun read(): UByte {
		synchronized(this.waiter) {
			this.waiter.wait()
			return this.char
		}
	}

	override fun write(d: UByte) {
		synchronized(this.waiter) {
			this.char = d
			this.waiter.notify()
		}
	}
}