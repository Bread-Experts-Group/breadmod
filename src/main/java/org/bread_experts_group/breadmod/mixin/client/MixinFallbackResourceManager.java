package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.IoSupplier;
import org.bread_experts_group.breadmod.BreadMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(FallbackResourceManager.class)
public class MixinFallbackResourceManager {
	@Inject(method = "wrapForDebug", at = @At("TAIL"), cancellable = true)
	private static void wrapForDebug(
			ResourceLocation location,
			PackResources packResources,
			IoSupplier<InputStream> stream,
			CallbackInfoReturnable<IoSupplier<InputStream>> cir
	) {
		if (location.getNamespace().equals(BreadMod.ID)) cir.setReturnValue(stream);
	}
}
