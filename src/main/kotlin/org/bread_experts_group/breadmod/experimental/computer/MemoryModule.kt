package org.bread_experts_group.breadmod.experimental.computer

/**
 * A memory module for a Bread Mod computer, with a settable capacity.
 *
 * **TODO**: Write to disk for very large capacities.
 *
 * **TODO**: Use an array solution with support for [UInt]s.
 * @param capacity The capacity of the memory module.
 * @throws IllegalArgumentException If the capacity is negative.
 * @since 1.0.0
 * @author Miko Elbrecht
 */
class MemoryModule(val capacity: UInt) {
	private val memory = Array(this.capacity.toInt()) {
		(0x0Fu).toUByte()
	}

	/**
	 * Gets the value at the specified address.
	 * @param address The address to read from.
	 * @return The value at the specified address.
	 * @throws ArrayIndexOutOfBoundsException If the address is out of bounds.
	 * @throws IllegalArgumentException If the address is negative.
	 * @since 1.0.0
	 * @see set
	 * @since 1.0.0
	 * @author Miko Elbrecht
	 */
	operator fun get(address: Int): UByte = this.memory[address]

	/**
	 * Sets the value at the specified address.
	 * @param address The address to write to.
	 * @param value The value to write.
	 * @throws ArrayIndexOutOfBoundsException If the address is out of bounds.
	 * @throws IllegalArgumentException If the address is negative.
	 * @since 1.0.0
	 * @see get
	 * @since 1.0.0
	 * @author Miko Elbrecht
	 */
	operator fun set(address: Int, value: UByte) {
		this.memory[address] = value
	}
}