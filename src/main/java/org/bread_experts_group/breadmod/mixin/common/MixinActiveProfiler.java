package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.util.profiling.ActiveProfiler;
import org.bread_experts_group.breadmod.BreadMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveProfiler.class)
public abstract class MixinActiveProfiler {
	@Shadow
	private boolean started;

	@SuppressWarnings("LongLine")
	@Inject(method = "push(Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V"), cancellable = true)
	private void pleaseShutUpPush(
			String name, CallbackInfo ci
	) {
		if (!name.contains(BreadMod.ID)) ci.cancel();
	}

	@SuppressWarnings("LongLine")
	@Inject(method = "pop", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;)V"), cancellable = true)
	private void pleaseShutUpPop(
			CallbackInfo ci
	) {
		if (!this.started) ci.cancel();
	}
}
