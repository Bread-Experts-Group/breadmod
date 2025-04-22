package org.bread_experts_group.breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.AABB

/**
 * Helper methods for scanning [BlockPos] in a specified area.
 */
object BlockScanner {
	val ALL_SIDES: List<Direction> = listOf(NORTH, SOUTH, EAST, WEST, UP, DOWN)

	fun scanAdjacent(pos: BlockPos, vararg directions: Direction): List<BlockPos> = directions.map(pos::relative)

	fun scanRadius(pos: BlockPos, radius: Double): List<BlockPos> = buildList {
		BlockPos.betweenClosedStream(AABB(pos).inflate(radius, radius, radius)).forEach { this.add(it.immutable()) }
	}

	fun scanArea(firstPos: BlockPos, secondPos: BlockPos): List<BlockPos> = buildList {
		BlockPos.betweenClosed(firstPos, secondPos).forEach { this.add(it.immutable()) }
	}

	fun List<BlockPos>.filterPositions(level: Level, filter: Block): List<BlockPos> =
		this.filter { level.getBlockState(it).`is`(filter) }
}