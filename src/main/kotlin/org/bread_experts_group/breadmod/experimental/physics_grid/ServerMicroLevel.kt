package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Supplier

/* Current Opcode stack (THIS CHANGES WHEN YOU ADD FIELDS & CONSTRUCTOR ARGS)
0, LocalVariable[name=this, slot=0, type=Lorg/bread_experts_group/breadmod/experimental/physics_grid/ServerMicroLevel;]
1, LocalVariable[name=sourceLevel, slot=1, type=Lnet/minecraft/world/level/Level;]
2, LocalVariable[name=blocks, slot=2, type=Ljava/util/Map;]
3, LocalVariable[name=blockEntities, slot=3, type=Ljava/util/Map;]
4, Label[context=CodeModel[id=1990572659], bci=0]
5, Load[OP=ALOAD_1, slot=1]
6, LoadConstant[OP=LDC, val=sourceLevel]
7, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
8, Load[OP=ALOAD_2, slot=2]
9, LoadConstant[OP=LDC, val=blocks]
10, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
11, Load[OP=ALOAD_3, slot=3]
12, LoadConstant[OP=LDC, val=blockEntities]
13, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
14, LineNumber[line=22]
15, Load[OP=ALOAD_0, slot=0]
16, LineNumber[line=26]
17, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
18, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
19, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
20, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
21, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
22, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
23, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
24, UnboundIntrinsicConstantInstruction[op=ICONST_0]
25, UnboundIntrinsicConstantInstruction[op=LCONST_0]
26, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
27, UnboundIntrinsicConstantInstruction[op=ICONST_1]
28, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
29, LineNumber[line=22]
30, Invoke[OP=INVOKESPECIAL, m=net/minecraft/server/level/ServerLevel.<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V]
31, LineNumber[line=23]
32, Load[OP=ALOAD_0, slot=0]
33, Load[OP=ALOAD_1, slot=1]
34, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/ServerMicroLevel.sourceLevel:Lnet/minecraft/world/level/Level;]
35, LineNumber[line=24]
36, Load[OP=ALOAD_0, slot=0]
37, Load[OP=ALOAD_2, slot=2]
38, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/ServerMicroLevel.blocks:Ljava/util/Map;]
39, LineNumber[line=25]
40, Load[OP=ALOAD_0, slot=0]
41, Load[OP=ALOAD_3, slot=3]
42, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/ServerMicroLevel.blockEntities:Ljava/util/Map;]
43, LineNumber[line=27]
44, Load[OP=ALOAD_0, slot=0]
45, LoadConstant[OP=LDC, val=PhysicsGrid]
46, Invoke[OP=INVOKESTATIC, m=org/apache/logging/log4j/LogManager.getLogger(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;]
47, UnboundStackInstruction[op=DUP]
48, LoadConstant[OP=LDC, val=getLogger(...)]
49, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V]
50, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/ServerMicroLevel.logger:Lorg/apache/logging/log4j/Logger;]
51, LineNumber[line=22]
52, Return[OP=RETURN]
53, Label[context=CodeModel[id=1990572659], bci=65]
 */

/**
 * The super constructor in this class is replaced at runtime with a no-args constructor via the breadmod agent.
 */
class ServerMicroLevel(
	private val sourceLevel: Level,
	val blocks: MutableMap<BlockPos, BlockState>,
	val blockEntities: MutableMap<BlockPos, BlockEntity>
) : ServerLevel(null, null, null, null, null, null, null, false, 0, null, true, null) {
	private val logger: Logger = LogManager.getLogger("PhysicsGrid")

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		this.blocks[pos] = state
		this.logger.fatal("nuclear bomb")
		return false
	}

	override fun dimensionTypeRegistration(): Holder<DimensionType> = this.sourceLevel.dimensionTypeRegistration()
	override fun getProfilerSupplier(): Supplier<ProfilerFiller> = this.sourceLevel.profilerSupplier

	override fun getMinBuildHeight(): Int = -64
	override fun getMaxBuildHeight(): Int = 365
	override fun hasChunk(chunkX: Int, chunkZ: Int): Boolean = true

	override fun getBlockState(pos: BlockPos): BlockState =
		this.blocks[pos] ?: Blocks.AIR.defaultBlockState()

	override fun getBlockEntity(pos: BlockPos): BlockEntity? = this.blockEntities[pos]

	override fun getFluidState(pos: BlockPos): FluidState = Fluids.EMPTY.defaultFluidState()

	override fun toString(): String = "ServerMicroLevel[blocks=${this.blocks.size}]"
}