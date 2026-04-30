package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ClassTreeIdRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SynchedEntityData.class)
public class MixinSynchedEntityData {
	@Shadow
	@Final
	static ClassTreeIdRegistry ID_REGISTRY;

	// Bypass poor forge coding
	@Inject(method = "defineId", at = @At("HEAD"), cancellable = true)
	private static void defineId(
			Class<? extends SyncedDataHolder> clazz, EntityDataSerializer<?> serializer,
			CallbackInfoReturnable<EntityDataAccessor<?>> cir
	) {
		int i = ID_REGISTRY.define(clazz);
		cir.setReturnValue(serializer.createAccessor(i));
	}
}
