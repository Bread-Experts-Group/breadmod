package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.ticks.LevelChunkTicks
import net.minecraft.world.ticks.LevelTicks
import net.minecraft.world.ticks.ScheduledTick
import java.util.PriorityQueue
import java.util.function.BiConsumer

class MicroLevelTicks<T : Any> : LevelTicks<T>(null, null) {
	private val tickOperations: MutableMap<Long, PriorityQueue<ScheduledTick<T>>> = mutableMapOf()
	private var thisOperation: PriorityQueue<ScheduledTick<T>>? = null
	override fun tick(gameTime: Long, maxAllowedTicks: Int, ticker: BiConsumer<BlockPos, T>) {
		val scheduled = this.tickOperations.remove(gameTime) ?: return
		this.thisOperation = scheduled
		var i = 0
		while (scheduled.isNotEmpty() && i++ < maxAllowedTicks) {
			val tick = scheduled.remove()
			ticker.accept(tick.pos, tick.type)
		}
	}

	override fun willTickThisTick(pos: BlockPos, type: T): Boolean {
		return this.thisOperation?.contains(ScheduledTick.probe(type, pos)) == true
	}

	override fun addContainer(chunkPos: ChunkPos, chunkTicks: LevelChunkTicks<T>) {
//		super.addContainer(chunkPos, chunkTicks)
		TODO("!")
	}

	override fun clearArea(area: BoundingBox) {
//		super.clearArea(area)
		TODO("!")
	}

	override fun copyArea(area: BoundingBox, offset: Vec3i) {
//		super.copyArea(area, offset)
		TODO("!")
	}

	override fun copyAreaFrom(levelTicks: LevelTicks<T>, area: BoundingBox, offset: Vec3i) {
//		super.copyAreaFrom(levelTicks, area, offset)
		TODO("!")
	}

	override fun count(): Int {
//		return super.count()
		TODO("!")
	}

	override fun hasScheduledTick(pos: BlockPos, type: T): Boolean {
//		return super.hasScheduledTick(pos, type)
		TODO("!")
	}

	override fun removeContainer(chunkPos: ChunkPos) {
//		super.removeContainer(chunkPos)
		TODO("!")
	}

	override fun schedule(tick: ScheduledTick<T>) {
		this.tickOperations.getOrPut(tick.triggerTick) {
			PriorityQueue(ScheduledTick.DRAIN_ORDER)
		}.add(tick)
	}
}