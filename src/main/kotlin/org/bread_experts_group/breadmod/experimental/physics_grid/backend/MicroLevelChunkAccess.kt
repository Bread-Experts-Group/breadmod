package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.LevelChunk

class MicroLevelChunkAccess(private val parent: ServerMicroLevel) : LevelChunk(parent, ChunkPos.ZERO)