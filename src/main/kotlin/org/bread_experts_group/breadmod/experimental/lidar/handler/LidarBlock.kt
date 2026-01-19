package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

class LidarBlock(private val blockPos: BlockPos) {
	val sides: MutableMap<Direction, LongArray> = mutableMapOf()

	fun getOrInitializeSide(direction: Direction): LongArray = this.sides.getOrPut(direction) {
		longArrayOf(
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000,
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000
		)
	}

	fun getArrayIndex(index: Int): Int = index / 64

	fun setBitForIndex(index: Int, direction: Direction, value: Boolean): Long {
		val longIndex: Int = index / 64
		val arrayIndex: Int = index - (longIndex * 64)
		val side = this.getOrInitializeSide(direction)
		val final = (side[longIndex] or (1L shl arrayIndex))
		side[longIndex] = if (value) final else final.inv()
		return side[longIndex]
	}

	fun getLong(index: Int, direction: Direction): Long = this.getOrInitializeSide(direction)[index]

	fun getBitString(long: Long): String = long.toULong().toString(2).padStart(64, '0')
}