package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.server.level.ServerChunkCache
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.status.ChunkStatus

class MicroLevelChunkSource(
	private val parent: ServerMicroLevel
) : ServerChunkCache(
	null, null, null, null,
	null, null, 0, 0,
	false, null, null, null
) {
	override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): ChunkAccess {
		return MicroLevelChunkAccess(this.parent)
	}
}