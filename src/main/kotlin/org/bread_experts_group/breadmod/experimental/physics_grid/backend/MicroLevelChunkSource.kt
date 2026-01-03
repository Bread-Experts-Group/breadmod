package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.server.level.ServerChunkCache
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.status.ChunkStatus
import java.util.function.BooleanSupplier

// todo ChunkMap, mekanism's BEs require it
class MicroLevelChunkSource(
	private val parent: ServerMicroLevel
) : ServerChunkCache(
	null, null, null, null,
	null, null, 0, 0,
	false, null, null, null
) {
	init {
		this.chunkMap = MicroLevelChunkMap(this.parent, this)
	}

	val singletonChunk: MicroLevelServerChunkAccess = MicroLevelServerChunkAccess(this.parent)
	override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): ChunkAccess {
		return this.singletonChunk
	}

	override fun tick(hasTimeLeft: BooleanSupplier, tickChunks: Boolean) {
		if (this.parent.tickRateManager().runsNormally()) this.parent.tickChunk(
			this.singletonChunk,
			this.parent.gameRules.getInt(GameRules.RULE_RANDOMTICKING)
		)
	}

	override fun hasChunk(x: Int, z: Int): Boolean = true
}