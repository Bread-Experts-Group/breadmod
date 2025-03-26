package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
abstract class MixinLevel implements LevelAccessor {
	@Inject(method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;", at = @At("HEAD"), cancellable = true)
	private void getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk, CallbackInfoReturnable<? super ChunkAccess> cir) {
//		int chunkLimit = (int) (this.getWorldBorder().getSize() / 16.0);
//		if (chunkLimit == 0) chunkLimit = 1;
//		int rX = 0; //((x + chunkLimit) % (chunkLimit * 2)) - chunkLimit;
//		int rZ = 0; //((z + chunkLimit) % (chunkLimit * 2)) - chunkLimit;
//		ChunkAccess chunkAccess = new EmptyLevelChunk(this., new ChunkPos(x, z), null);
//		if (chunkAccess == null && requireChunk)
//			throw new IllegalStateException("Should always be able to create a chunk!");
//		cir.setReturnValue(chunkAccess);
	}
}
