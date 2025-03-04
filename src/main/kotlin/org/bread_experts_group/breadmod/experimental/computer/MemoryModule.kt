package org.bread_experts_group.breadmod.experimental.computer

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.ByteArrayTag
import net.neoforged.neoforge.common.util.INBTSerializable

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
class MemoryModule(val capacity: UInt) : INBTSerializable<ByteArrayTag> {
	@OptIn(ExperimentalUnsignedTypes::class)
	private var memory = UByteArray(this.capacity.toInt()) {
		(0x00u).toUByte()
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
	@OptIn(ExperimentalUnsignedTypes::class)
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
	@OptIn(ExperimentalUnsignedTypes::class)
	operator fun set(address: Int, value: UByte) {
		this.memory[address] = value
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	override fun serializeNBT(provider: HolderLookup.Provider): ByteArrayTag =
		ByteArrayTag(this.memory.toByteArray())

	@OptIn(ExperimentalUnsignedTypes::class)
	override fun deserializeNBT(provider: HolderLookup.Provider, tag: ByteArrayTag) {
		this.memory = tag.asByteArray.toUByteArray()
	}
}