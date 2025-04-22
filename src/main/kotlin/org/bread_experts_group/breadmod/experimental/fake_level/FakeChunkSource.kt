package org.bread_experts_group.breadmod.experimental.fake_level

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

class FakeChunkSource(private val fakeLevel: FakeLevel) : ChunkSource() {
	private val chunks: Long2ObjectMap<FakeChunk> = Long2ObjectOpenHashMap()

	override fun getLevel(): BlockGetter = this.fakeLevel

	fun getChunk(x: Int, z: Int): FakeChunk =
		this.chunks.computeIfAbsent(ChunkPos.asLong(x, z), LongFunction { FakeChunk(this.fakeLevel, x, z) })

	override fun getChunk(p0: Int, p1: Int, p2: ChunkStatus, p3: Boolean): ChunkAccess = this.getChunk(p0, p1)
	override fun getChunk(chunkX: Int, chunkZ: Int, load: Boolean): LevelChunk? = null

	override fun tick(p0: BooleanSupplier, p1: Boolean) {}

	override fun gatherStats(): String = "FakeChunkSource"

	override fun getLoadedChunksCount(): Int = 0

	override fun getLightEngine(): LevelLightEngine = this.fakeLevel.lightEngine
}