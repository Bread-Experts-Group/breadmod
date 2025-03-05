package org.bread_experts_group.breadmod.experimental.computer

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.readBinary
import org.bread_experts_group.breadmod.experimental.computer.bios.BIOSProvider
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc
import org.bread_experts_group.breadmod.experimental.computer.io.BreadModPollingVirtualKeyboard
import org.bread_experts_group.breadmod.experimental.computer.io.Diagnostics
import org.bread_experts_group.breadmod.experimental.computer.io.IODevice
import org.bread_experts_group.breadmod.experimental.computer.io.ps2.PS2Controller
import org.bread_experts_group.breadmod.experimental.computer.io.ps2.PS2SystemControllerA

/**
 * A Bread Mod computer.
 * @since 1.0.0
 * @see MemoryModule
 * @see Processor
 * @author Miko Elbrecht
 */
class Computer(
	val memory: List<MemoryModule>,
	val processor: Processor,
	val bios: BIOSProvider
) : SimulationSteppable, INBTSerializable<CompoundTag> {
	val keyboard: BreadModPollingVirtualKeyboard = BreadModPollingVirtualKeyboard()
	var disc: ISO9660Disc? = null
	val ps2: PS2Controller = PS2Controller()
	val ioMap: MutableMap<UInt, IODevice> = mutableMapOf(
		0x60u to this.ps2.data,
		0x64u to this.ps2.command,
		0x80u to Diagnostics(),
		0x92u to PS2SystemControllerA(),
		0xB30D0000u to this.keyboard
	)

	fun requestMemoryAt(address: ULong): UByte {
		var count = 0u
		val module = this.memory.firstOrNull { count += it.capacity; count > address }
			?: return UByte.MIN_VALUE
		return module[(address - count + module.capacity).toInt()]
	}

	fun setMemoryAt(address: ULong, data: UByte) {
		var count = 0u
		val module = this.memory.firstOrNull { count += it.capacity; count > address }
			?: throw IndexOutOfBoundsException("Memory address out of bounds, $address while setting $data")
		module[(address - count + module.capacity).toInt()] = data
	}

	fun setMemoryAt32(address: ULong, value: UInt) {
		this.setMemoryAt(address, (value and 0xFFu).toUByte())
		this.setMemoryAt(address + 1u, ((value shr 8) and 0xFFu).toUByte())
		this.setMemoryAt(address + 2u, ((value shr 16) and 0xFFu).toUByte())
		this.setMemoryAt(address + 3u, ((value shr 24) and 0xFFu).toUByte())
	}

	fun setMemoryAt16(address: ULong, value: UShort) {
		val v = value.toUInt()
		this.setMemoryAt(address, (v and 0xFFu).toUByte())
		this.setMemoryAt(address + 1u, ((v shr 8) and 0xFFu).toUByte())
	}

	fun requestMemoryAt16(address: ULong): UShort {
		var offset = 0u
		return readBinary(2, { this.requestMemoryAt(address + offset).also { offset++ } }).toUShort()
	}

	fun requestMemoryAt32(address: ULong): UInt {
		var offset = 0u
		return readBinary(4, { this.requestMemoryAt(address + offset).also { offset++ } }).toUInt()
	}

	fun requestMemoryAt64(address: ULong): ULong {
		var offset = 0u
		return readBinary(8, { this.requestMemoryAt(address + offset).also { offset++ } }).toULong()
	}

	override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag = CompoundTag().also {
		it.put("processor", this.processor.serializeNBT(provider))
		it.put("memory", CompoundTag().also { mt ->
			this.memory.forEachIndexed { i, m ->
				mt.put(i.toString(), m.serializeNBT(provider))
			}
		})
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, tag: CompoundTag) {
		this.processor.deserializeNBT(provider, tag)
		val memoryTag = tag.getCompound("memory")
		memoryTag.allKeys.forEach {
			this.memory[it.toInt()].deserializeNBT(provider, memoryTag.get(it) as ByteArrayTag)
		}
	}

	override fun reset() {
		this.bios.initialize(this)
		this.processor.reset()
	}

	override fun step() {
		this.processor.step()
	}
}