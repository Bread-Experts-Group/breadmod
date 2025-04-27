package org.bread_experts_group.breadmod.experimental.physics_grid.dummy_level

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkSource
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.lighting.LevelLightEngine
import java.util.function.BooleanSupplier
import java.util.function.LongFunction

class DummyChunkSource(private val level: DummyLevel) : ChunkSource() {
	private val chunks: Long2ObjectMap<DummyChunk> = Long2ObjectOpenHashMap()

	override fun getLevel(): BlockGetter = this.level
	override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): ChunkAccess =
		this.getChunk(x, z)

	fun getChunk(x: Int, z: Int): DummyChunk =
		this.chunks.computeIfAbsent(ChunkPos.asLong(x, z), LongFunction { DummyChunk(x, z, this.level) })

	override fun getChunk(chunkX: Int, chunkZ: Int, load: Boolean): LevelChunk? = null

	override fun tick(hasTimeLeft: BooleanSupplier, tickChunks: Boolean) {}
	override fun gatherStats(): String = "DummyChunkSource"
	override fun getLoadedChunksCount(): Int = 0
	override fun getLightEngine(): LevelLightEngine = this.level.lightEngine
}