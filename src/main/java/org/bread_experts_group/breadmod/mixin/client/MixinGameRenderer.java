package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.bread_experts_group.breadmod.client.render.RenderGeneralKt;
import org.bread_experts_group.breadmod.registry.shader.ModPostChains;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V"))
	private void render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
		RenderGeneralKt.renderBlend(deltaTracker);
	}

	@Inject(method = "resize", at = @At("TAIL"))
	private void resize(int width, int height, CallbackInfo ci) {
		if (ModPostChains.INSTANCE.getReady()) {
			ModPostChains.INSTANCE.resize(width, height);
		}
	}
}
