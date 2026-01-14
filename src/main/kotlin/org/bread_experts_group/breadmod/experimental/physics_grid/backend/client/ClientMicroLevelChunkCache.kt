package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.status.ChunkStatus

class ClientMicroLevelChunkCache(val parent: ClientMicroLevel) : ClientChunkCache(null, 0) {
	private val singletonChunk: ClientMicroLevelChunk = ClientMicroLevelChunk(this.parent)
	override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): LevelChunk =
		this.singletonChunk
}