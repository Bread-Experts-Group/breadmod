package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.server.level.progress.LoggerChunkProgressListener
import net.minecraft.world.Difficulty
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.EmptyLevelChunk
import net.minecraft.world.level.chunk.ImposterProtoChunk
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraft.world.level.timers.TimerQueue
import net.minecraft.world.level.validation.DirectoryValidator
import org.bread_experts_group.breadmod.client.render.localClient
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.stream.Stream
import kotlin.io.path.Path

class MicroLevel : ServerLevel(
	localClient.singleplayerServer, Executors.newVirtualThreadPerTaskExecutor(),
	LevelStorageSource(
		Path("./testalpha"),
		Path("./testbeta"),
		DirectoryValidator({ _ -> true }),
		null
	).createAccess("123ExpExpHelpMe"),
	object : ServerLevelData {
		override fun setThundering(p0: Boolean) {
			TODO("Not yet implemented")
		}

		override fun getRainTime(): Int {
			TODO("Not yet implemented")
		}

		override fun setRainTime(p0: Int) {
			TODO("Not yet implemented")
		}

		override fun setThunderTime(p0: Int) {
			TODO("Not yet implemented")
		}

		override fun getThunderTime(): Int {
			TODO("Not yet implemented")
		}

		override fun getClearWeatherTime(): Int {
			TODO("Not yet implemented")
		}

		override fun setClearWeatherTime(p0: Int) {
			TODO("Not yet implemented")
		}

		override fun getWanderingTraderSpawnDelay(): Int {
			TODO("Not yet implemented")
		}

		override fun setWanderingTraderSpawnDelay(p0: Int) {
			TODO("Not yet implemented")
		}

		override fun getWanderingTraderSpawnChance(): Int {
			TODO("Not yet implemented")
		}

		override fun setWanderingTraderSpawnChance(p0: Int) {
			TODO("Not yet implemented")
		}

		override fun getWanderingTraderId(): UUID? {
			TODO("Not yet implemented")
		}

		override fun setWanderingTraderId(p0: UUID) {
			TODO("Not yet implemented")
		}

		override fun getGameType(): GameType {
			TODO("Not yet implemented")
		}

		override fun setWorldBorder(p0: WorldBorder.Settings) {
			TODO("Not yet implemented")
		}

		override fun isInitialized(): Boolean {
			TODO("Not yet implemented")
		}

		override fun setInitialized(p0: Boolean) {
			TODO("Not yet implemented")
		}

		override fun isAllowCommands(): Boolean {
			TODO("Not yet implemented")
		}

		override fun setGameType(p0: GameType) {
			TODO("Not yet implemented")
		}

		override fun getScheduledEvents(): TimerQueue<MinecraftServer> = TODO("Not yet implemented")

		override fun setGameTime(p0: Long) {
			localClient.level!!.gameTime = p0
		}

		override fun setDayTime(p0: Long) {
			localClient.level!!.dayTime = p0
		}

		override fun setDayTimeFraction(p0: Float) {
			localClient.level!!.dayTimeFraction = p0
		}

		override fun setDayTimePerTick(p0: Float) {
			localClient.level!!.dayTimePerTick = p0
		}

		override fun setSpawn(p0: BlockPos, p1: Float) {
			localClient.level!!.setDefaultSpawnPos(p0, p1)
		}

		override fun setRaining(p0: Boolean) {
			TODO("Not yet implemented")
		}

		override fun getLevelName(): String = "123ExpExpHelpMeLVLNME"
		override fun getDayTimeFraction(): Float = localClient.level!!.dayTimeFraction
		override fun getDayTimePerTick(): Float = localClient.level!!.dayTimePerTick
		override fun getWorldBorder(): WorldBorder.Settings = TODO("Not yet implemented")
		override fun getSpawnPos(): BlockPos = localClient.level!!.sharedSpawnPos
		override fun getSpawnAngle(): Float = localClient.level!!.sharedSpawnAngle
		override fun getGameTime(): Long = localClient.level!!.gameTime
		override fun getDayTime(): Long = localClient.level!!.dayTime
		override fun isThundering(): Boolean = localClient.level!!.isThundering
		override fun isRaining(): Boolean = localClient.level!!.isRaining
		override fun isHardcore(): Boolean = TODO("Not yet implemented")
		override fun getGameRules(): GameRules = localClient.level!!.gameRules
		override fun getDifficulty(): Difficulty = localClient.level!!.difficulty
		override fun isDifficultyLocked(): Boolean = TODO("Not yet implemented")
	}, localClient.level!!.dimension(), LevelStem(
		Holder.direct(localClient.level!!.dimensionType()),
		object : ChunkGenerator(
			object : BiomeSource() {
				override fun codec(): MapCodec<out BiomeSource> {
					TODO("Not yet implemented")
				}

				override fun collectPossibleBiomes(): Stream<Holder<Biome>> = Stream.empty()

				override fun getNoiseBiome(
					p0: Int,
					p1: Int,
					p2: Int,
					p3: Climate.Sampler
				): Holder<Biome> {
					return Holder.direct(null)
				}
			}
		) {
			override fun codec(): MapCodec<out ChunkGenerator?> {
				TODO("Not yet implemented")
			}

			override fun applyCarvers(
				p0: WorldGenRegion,
				p1: Long,
				p2: RandomState,
				p3: BiomeManager,
				p4: StructureManager,
				p5: ChunkAccess,
				p6: GenerationStep.Carving
			) {
				// TODO !??!
			}

			override fun buildSurface(
				p0: WorldGenRegion,
				p1: StructureManager,
				p2: RandomState,
				p3: ChunkAccess
			) {
				// TODO !??!
			}

			override fun spawnOriginalMobs(p0: WorldGenRegion) {
				// TODO !??!
			}

			override fun getGenDepth(): Int {
				TODO("Not yet implemented")
			}

			override fun fillFromNoise(
				p0: Blender,
				p1: RandomState,
				p2: StructureManager,
				p3: ChunkAccess
			): CompletableFuture<ChunkAccess> = CompletableFuture.supplyAsync {
				ImposterProtoChunk(EmptyLevelChunk(localClient.level, ChunkPos.ZERO, null), true)
			}

			override fun getSeaLevel(): Int {
				TODO("Not yet implemented")
			}

			override fun getMinY(): Int {
				TODO("Not yet implemented")
			}

			override fun getBaseHeight(
				p0: Int,
				p1: Int,
				p2: Heightmap.Types,
				p3: LevelHeightAccessor,
				p4: RandomState
			): Int {
				TODO("Not yet implemented")
			}

			override fun getBaseColumn(
				p0: Int,
				p1: Int,
				p2: LevelHeightAccessor,
				p3: RandomState
			): NoiseColumn {
				TODO("Not yet implemented")
			}

			override fun addDebugScreenInfo(
				p0: List<String?>,
				p1: RandomState,
				p2: BlockPos
			) {
				TODO("Not yet implemented")
			}
		}
	),
	LoggerChunkProgressListener.createCompleted(), false, 0,
	listOf(), false, null
)