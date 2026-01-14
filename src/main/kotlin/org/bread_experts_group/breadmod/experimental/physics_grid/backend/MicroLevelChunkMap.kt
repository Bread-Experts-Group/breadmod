package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.server.level.ChunkHolder
import net.minecraft.server.level.ChunkMap
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.LevelChunk
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevelChunkCache

class MicroLevelChunkMap(
	val microLevel: ServerMicroLevel,
	val microChunkCache: ServerMicroLevelChunkCache
) : ChunkMap(
	microLevel, null, null,
	null, null, null,
	null, null, null,
	null, null, 16, false
) {
	val singletonHolder: ChunkHolder = object : ChunkHolder(
		ChunkPos.ZERO,
		0,
		this.microLevel,
		this.microLevel.lightEngine,
		{ pos, getter, ticket, sender -> },
		this
	) {
		override fun getTickingChunk(): LevelChunk = this@MicroLevelChunkMap.microChunkCache.singletonChunk
	}

	public override fun getChunks(): Iterable<ChunkHolder> = listOf(this.singletonHolder)
}