package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.ticks.LevelChunkTicks
import net.minecraft.world.ticks.LevelTicks
import net.minecraft.world.ticks.ScheduledTick
import java.util.PriorityQueue
import java.util.function.BiConsumer

class ServerMicroLevelBlockTicks(
	private val getGameTime: () -> Long
) : LevelTicks<Block>(null, null) {
	private val tickOperations: MutableMap<Long, PriorityQueue<ScheduledTick<Block>>> = mutableMapOf()
	private val lastOperation: MutableList<ScheduledTick<Block>> = mutableListOf()
	private var lastOperationRelevantTo: Long = 0
	override fun tick(gameTime: Long, maxAllowedTicks: Int, ticker: BiConsumer<BlockPos, Block>) {
		val scheduled = this.tickOperations.remove(gameTime) ?: return
		this.lastOperation.clear()
		this.lastOperationRelevantTo = gameTime
		var i = 0
		while (scheduled.isNotEmpty() && i++ < maxAllowedTicks) {
			val tick = scheduled.remove()
			this.lastOperation.add(tick)
			ticker.accept(tick.pos, tick.type)
		}
	}

	override fun willTickThisTick(pos: BlockPos, type: Block): Boolean {
		if (this.lastOperationRelevantTo != this.getGameTime()) return false
		return this.lastOperation.any { it.pos == pos && it.type == type }
	}

	override fun addContainer(chunkPos: ChunkPos, chunkTicks: LevelChunkTicks<Block?>) {
//		super.addContainer(chunkPos, chunkTicks)
		println("addContainer $chunkPos, $chunkTicks")
	}

	override fun clearArea(area: BoundingBox) {
//		super.clearArea(area)
		println("clearArea $area")
	}

	override fun copyArea(area: BoundingBox, offset: Vec3i) {
//		super.copyArea(area, offset)
		println("copyArea $area, $offset")
	}

	override fun copyAreaFrom(levelTicks: LevelTicks<Block?>, area: BoundingBox, offset: Vec3i) {
//		super.copyAreaFrom(levelTicks, area, offset)
		println("copyAreaFrom $levelTicks, $area, $offset")
	}

	override fun count(): Int {
//		return super.count()
		println("count")
		return 0
	}

	override fun hasScheduledTick(pos: BlockPos, type: Block): Boolean {
//		return super.hasScheduledTick(pos, type)
		println("hasScheduledTick $pos, $type")
		return false
	}

	override fun removeContainer(chunkPos: ChunkPos) {
//		super.removeContainer(chunkPos)
		println("removeContainer $chunkPos")
	}

	override fun schedule(tick: ScheduledTick<Block>) {
		this.tickOperations.getOrPut(tick.triggerTick) {
			PriorityQueue(
				Comparator { a, b ->
					val initialPriority = a.priority.compareTo(b.priority)
					if (initialPriority == 0) a.subTickOrder.compareTo(b.subTickOrder)
					else initialPriority
				}
			)
		}.add(tick)
	}
}