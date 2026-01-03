package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.client.multiplayer.ClientLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

class ClientMicroLevel(
	private val grid: PhysicsGrid
) : ClientLevel(
	null, null, null, null,
	0, 0, null, null, false,
	0
) {
	private val chunkSource: ClientMicroLevelChunkSource = ClientMicroLevelChunkSource(this)
	override fun getChunkSource(): ClientChunkCache = this.chunkSource
}