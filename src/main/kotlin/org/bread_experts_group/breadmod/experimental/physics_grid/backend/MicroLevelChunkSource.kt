package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.server.level.ServerChunkCache

class MicroLevelChunkSource(
	private val parent: ServerMicroLevel
) : ServerChunkCache(
	null, null, null, null,
	null, null, 0, 0,
	false, null, null, null
)