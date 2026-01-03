package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.server.level.ChunkMap

/*
0, LocalVariable[name=this, slot=0, type=Lorg/bread_experts_group/breadmod/experimental/physics_grid/backend/MicroLevelChunkMap;]
1, LocalVariable[name=microLevel, slot=1, type=Lorg/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel;]
2, LocalVariable[name=microChunkCache, slot=2, type=Lorg/bread_experts_group/breadmod/experimental/physics_grid/backend/MicroLevelChunkSource;]
3, Label[context=CodeModel[id=1757298299], bci=0]
4, Load[OP=ALOAD_1, slot=1]
5, LoadConstant[OP=LDC, val=microLevel]
6, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
7, Load[OP=ALOAD_2, slot=2]
8, LoadConstant[OP=LDC, val=microChunkCache]
9, Invoke[OP=INVOKESTATIC, m=kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V]
10, LineNumber[line=5]
11, Load[OP=ALOAD_0, slot=0]
12, LineNumber[line=9]
13, Load[OP=ALOAD_1, slot=1]
14, TypeCheck[OP=CHECKCAST, type=net/minecraft/server/level/ServerLevel]
15, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
16, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
17, LineNumber[line=10]
18, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
19, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
20, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
21, LineNumber[line=11]
22, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
23, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
24, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
25, LineNumber[line=12]
26, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
27, UnboundIntrinsicConstantInstruction[op=ACONST_NULL]
28, ArgumentConstant[OP=BIPUSH, val=16]
29, UnboundIntrinsicConstantInstruction[op=ICONST_0]
30, LineNumber[line=5]
31, Invoke[OP=INVOKESPECIAL, m=net/minecraft/server/level/ChunkMap.<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Ljava/util/concurrent/Executor;Lnet/minecraft/util/thread/BlockableEventLoop;Lnet/minecraft/world/level/chunk/LightChunkGetter;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/server/level/progress/ChunkProgressListener;Lnet/minecraft/world/level/entity/ChunkStatusUpdateListener;Ljava/util/function/Supplier;IZ)V]
32, LineNumber[line=6]
33, Load[OP=ALOAD_0, slot=0]
34, Load[OP=ALOAD_1, slot=1]
35, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/MicroLevelChunkMap.microLevel:Lorg/bread_experts_group/breadmod/experimental/physics_grid/backend/ServerMicroLevel;]
36, LineNumber[line=7]
37, Load[OP=ALOAD_0, slot=0]
38, Load[OP=ALOAD_2, slot=2]
39, Field[OP=PUTFIELD, field=org/bread_experts_group/breadmod/experimental/physics_grid/backend/MicroLevelChunkMap.microChunkCache:Lorg/bread_experts_group/breadmod/experimental/physics_grid/backend/MicroLevelChunkSource;]
40, LineNumber[line=5]
41, Return[OP=RETURN]
42, Label[context=CodeModel[id=1757298299], bci=44]
 */
class MicroLevelChunkMap(
	val microLevel: ServerMicroLevel,
	val microChunkCache: MicroLevelChunkSource
) : ChunkMap(
	microLevel, null, null,
	null, null, null,
	null, null, null,
	null, null, 16, false
)