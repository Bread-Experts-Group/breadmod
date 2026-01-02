package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.AbortableIterationConsumer
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.TickRateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.ticks.LevelTicks
import net.neoforged.neoforge.common.CommonHooks
import net.neoforged.neoforge.entity.PartEntity
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.executeOnRenderThread
import org.bread_experts_group.breadmod.experimental.physics_grid.BlockNamesHuffmanSavedData
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.util.logDebugInfo
import java.util.UUID
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import java.util.function.Supplier

/* Current Opcode stack (THIS CHANGES WHEN YOU ADD FIELDS & CONSTRUCTOR ARGS)
0, LocalVariable[name=this, slot=0, type=Lorg/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel;]
1, LocalVariable[name=grid, slot=1, type=Lorg/bread_experts_group/breadmod/experimental/physics_grid/PhysicsGrid;]
2, LocalVariable[name=sourceLevel, slot=2, type=Lnet/minecraft/world/level/Level;]
3, LocalVariable[name=blocks, slot=3, type=Ljava/util/Map;]
4, LocalVariable[name=blockEntities, slot=4, type=Ljava/util/Map;]
5, Label[context=CodeModel[id=555314014], bci=0]
6, Load[OP=ALOAD_1, slot=1]
7, LoadConstant[OP=LDC, val=grid]
8, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
9, Load[OP=ALOAD_2, slot=2]
10, LoadConstant[OP=LDC, val=sourceLevel]
11, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
12, Load[OP=ALOAD_3, slot=3]
13, LoadConstant[OP=LDC, val=blocks]
14, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
15, Load[OP=ALOAD, slot=4]
16, LoadConstant[OP=LDC, val=blockEntities]
17, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
18, LineNumber[line=86]
19, Load[OP=ALOAD_0, slot=0]
20, LineNumber[line=91]
21, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
22, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
23, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
24, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
25, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
26, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
27, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
28, UnboundIntrinsicConstantInstruction[op=ICONST_0]
29, UnboundIntrinsicConstantInstruction[op=LCONST_0]
30, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
31, UnboundIntrinsicConstantInstruction[op=ICONST_1]
32, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
33, LineNumber[line=86]
34, Invoke[OP=INVOKESPECIAL, m=net/minecraft/server/level/ServerLevel.<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V]
35, LineNumber[line=87]
36, Load[OP=ALOAD_0, slot=0]
37, Load[OP=ALOAD_1, slot=1]
38, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.grid:Lorg/bread_experts_group/breadmod/experimental/physics_grid/PhysicsGrid;]
39, LineNumber[line=88]
40, Load[OP=ALOAD_0, slot=0]
41, Load[OP=ALOAD_2, slot=2]
42, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.sourceLevel:Lnet/minecraft/world/level/Level;]
43, LineNumber[line=89]
44, Load[OP=ALOAD_0, slot=0]
45, Load[OP=ALOAD_3, slot=3]
46, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.blocks:Ljava/util/Map;]
47, LineNumber[line=90]
48, Load[OP=ALOAD_0, slot=0]
49, Load[OP=ALOAD, slot=4]
50, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.blockEntities:Ljava/util/Map;]
51, LineNumber[line=92]
52, Load[OP=ALOAD_0, slot=0]
53, LoadConstant[OP=LDC, val=PhysicsGrid]
54, Invoke[OP=INVOKESTATIC, m=org/apache/logging/log4j/LogManager.getLogger(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;]
55, UnboundStackInstruction[op=DUP]
56, LoadConstant[OP=LDC, val=getLogger(...)]
57, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V]
58, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.logger:Lorg/apache/logging/log4j/Logger;]
59, LineNumber[line=94]
60, UnboundNopInstruction[op=NOP]
61, LineNumber[line=95]
62, Load[OP=ALOAD_0, slot=0]
63, NewObj[OP=NEW, type=net/minecraft/world/level/redstone/CollectingNeighborUpdater]
64, UnboundStackInstruction[op=DUP]
65, Load[OP=ALOAD_0, slot=0]
66, TypeCheck[OP=CHECKCAST, type=net/minecraft/world/level/Level]
67, LoadConstant[OP=LDC, val=100000]
68, Invoke[OP=INVOKESPECIAL, m=net/minecraft/world/level/redstone/CollectingNeighborUpdater.<init>(Lnet/minecraft/world/level/Level;I)V]
69, TypeCheck[OP=CHECKCAST, type=net/minecraft/world/level/redstone/NeighborUpdater]
70, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.neighborUpdater:Lnet/minecraft/world/level/redstone/NeighborUpdater;]
71, LineNumber[line=96]
72, Load[OP=ALOAD_0, slot=0]
73, Invoke[OP=INVOKESTATIC, m=net/minecraft/util/RandomSource.createThreadSafe()Lnet/minecraft/util/RandomSource;]
74, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.threadSafeRandom:Lnet/minecraft/util/RandomSource;]
75, LineNumber[line=97]
76, Load[OP=ALOAD_0, slot=0]
77, Invoke[OP=INVOKESTATIC, m=net/minecraft/util/RandomSource.create()Lnet/minecraft/util/RandomSource;]
78, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel.random:Lnet/minecraft/util/RandomSource;]
79, LineNumber[line=98]
80, UnboundNopInstruction[op=NOP]
81, LineNumber[line=86]
82, Return[OP=RETURN]
83, Label[context=CodeModel[id=555314014], bci=114]
 */

/**
 * The super constructor in this class is replaced at runtime with a no-args constructor via the breadmod agent.
 */
class ServerMicroLevel(
	private val grid: PhysicsGrid,
	private val sourceLevel: Level,
	val blocks: MutableMap<BlockPos, BlockState>,
	val blockEntities: MutableMap<BlockPos, BlockEntity>
) : ServerLevel(null, null, null, null, null, null, null, false, 0, null, true, null) {
	companion object {
		fun getLevelBaseForData(server: MinecraftServer): ServerLevel? = server.getLevel(OVERWORLD)
		fun getNameSpaceAndNameHuffmanSD(server: MinecraftServer): BlockNamesHuffmanSavedData? {
			val dataBase = this.getLevelBaseForData(server) ?: return null
			return dataBase.dataStorage.get(
				BlockNamesHuffmanSavedData.FACTORY,
				"__beg_microlevel_blocks_huffman"
			)
		}

		fun computeNameSpaceAndNameHuffmanSD(server: MinecraftServer) {
			val dataBase = this.getLevelBaseForData(server) ?: return
			dataBase.dataStorage.set(
				"__beg_microlevel_blocks_huffman",
				BlockNamesHuffmanSavedData.create(BuiltInRegistries.BLOCK)
			)
		}
	}

	private val logger: Logger = LogManager.getLogger("PhysicsGrid")

	init {
		this.players = this.grid.playersInGrid
	}

	override fun players(): List<ServerPlayer> = this.grid.playersInGrid

	fun initBlockEntities() {
		this.blockEntities.forEach { (_, entity) -> entity.level = this }
		this.blockEntities.values.forEach {
			logDebugInfo(this.getChunkAt(it.blockPos))
			this.setBlockEntity(it)
		}
	}

	override fun dimensionType(): DimensionType = this.sourceLevel.dimensionType()

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		this.logger.fatal("nuclear bomb")
		var oldState = this.blocks[pos]
		if (oldState == state) return false
		if (oldState == null) oldState = Blocks.AIR.defaultBlockState()
		else oldState.onRemove(this, pos, state, false)
		this.blocks[pos] = state
		state.onPlace(this, pos, oldState, false)
		// todo test recompiling
		/*if (this.sourceLevel.isClientSide)*/ executeOnRenderThread {
			PhysicsGrid.Companion.gridMeshes.forEach { (_, mesh) -> mesh.markForRecompile() }
		}
		return false
	}

	override fun dimensionTypeRegistration(): Holder<DimensionType> = this.sourceLevel.dimensionTypeRegistration()
	override fun getProfilerSupplier(): Supplier<ProfilerFiller> = this.sourceLevel.profilerSupplier
	override fun getProfiler(): ProfilerFiller = this.sourceLevel.profiler

	override fun getMinBuildHeight(): Int = -64
	override fun getMaxBuildHeight(): Int = 365
	override fun hasChunk(chunkX: Int, chunkZ: Int): Boolean = true
	override fun enabledFeatures(): FeatureFlagSet = this.sourceLevel.enabledFeatures()
	override fun mayInteract(player: Player, pos: BlockPos): Boolean = true

	override fun getBlockState(pos: BlockPos): BlockState = this.blocks[pos] ?: Blocks.AIR.defaultBlockState()
	override fun getBlockEntity(pos: BlockPos): BlockEntity? = this.blockEntities[pos]
	override fun getFluidState(pos: BlockPos): FluidState = Fluids.EMPTY.defaultFluidState() // TODO: Fluids

	override fun getEntities(): LevelEntityGetter<Entity> = object : LevelEntityGetter<Entity> {
		override fun <U : Entity> get(
			test: EntityTypeTest<Entity, U>,
			bounds: AABB,
			consumer: AbortableIterationConsumer<U>
		) {
		}

		override fun <U : Entity> get(test: EntityTypeTest<Entity, U>, consumer: AbortableIterationConsumer<U>) {
		}

		override fun get(boundingBox: AABB, consumer: Consumer<Entity>) {
		}

		override fun get(id: Int): Entity? = null
		override fun get(uuid: UUID): Entity? = null
		override fun getAll(): Iterable<Entity> = emptyList()
	} // TODO: Entities

	override fun getPartEntities(): Collection<PartEntity<*>> = emptyList() // TODO: Part entities

	override fun getChunkSource(): ServerChunkCache = MicroLevelChunkSource(this)
	override fun getWorldBorder(): WorldBorder = WorldBorder()

	// Ticking
	private val events: ArrayDeque<MicroLevelBlockEvent> = ArrayDeque()
	private val blockTicks: LevelTicks<Block> = ServerMicroLevelBlockTicks(this::getGameTime)
	override fun getBlockTicks(): LevelTicks<Block> = this.blockTicks
	override fun tick(hasTimeLeft: BooleanSupplier) {
		this.blockTicks.tick(this.gameTime, 65536, this::tickBlock)
		while (this.events.isNotEmpty()) {
			val (pos, block, eventID, eventParam) = this.events.removeLast()
			val state = this.getBlockState(pos)
			if (state.`is`(block) && state.triggerEvent(this, pos, eventID, eventParam)) {
//				val position = pos.toVec3() + this.grid.pos
//				this.sourceLevel.server?.playerList?.broadcast(
//					null,
//					position.x,
//					position.y,
//					position.z,
//					64.0,
//					this.sourceLevel.dimension(),
//					ClientboundBlockEventPacket(
//						BlockPos(position.toVec3i()),
//						block,
//						eventID,
//						eventParam
//					)
//				) TODO: This packet must contain the local grid, as it is sent from the server. For now, playing locally..
			}
		}
		this.tickBlockEntities()
	}

	override fun tickRateManager(): TickRateManager = object : TickRateManager() {
		override fun runsNormally(): Boolean = true
	}

	override fun playSeededSound(
		player: Player?,
		x: Double,
		y: Double,
		z: Double,
		sound: Holder<SoundEvent?>,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		seed: Long
	): Unit = this.sourceLevel.playSeededSound(
		player,
		x + this.grid.pos.x,
		y + this.grid.pos.y,
		z + this.grid.pos.z,
		sound, category, volume, pitch, seed
	)

	override fun blockEvent(pos: BlockPos, block: Block, eventID: Int, eventParam: Int) {
		this.events.add(MicroLevelBlockEvent(pos, block, eventID, eventParam))
	}

	override fun levelEvent(player: Player?, type: Int, pos: BlockPos, data: Int) {
		println("e $player, $type, $pos, $data")
	}

	private val gameEventDispatcher: MicroLevelGameEventDispatcher = MicroLevelGameEventDispatcher(this)
	override fun gameEvent(gameEvent: Holder<GameEvent>, pos: Vec3, context: GameEvent.Context) {
		if (CommonHooks.onVanillaGameEvent(this, gameEvent, pos, context))
			this.gameEventDispatcher.post(gameEvent, pos, context)
	}

	override fun toString(): String = "ServerMicroLevel[blocks=${this.blocks.size}]"
	override fun registryAccess(): RegistryAccess = this.sourceLevel.registryAccess()
	override fun getGameTime(): Long = this.sourceLevel.gameTime
	override fun getLevelData(): LevelData = this.sourceLevel.levelData
}