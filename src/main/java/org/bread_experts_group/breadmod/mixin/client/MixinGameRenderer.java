package org.bread_experts_group.breadmod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.bread_experts_group.breadmod.client.render.RenderGeneralKt;
import org.bread_experts_group.breadmod.registry.shader.ModPostChains;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
	@Shadow
	@Final
	Minecraft minecraft;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", ordinal = 0, shift = At.Shift.BEFORE))
	private void render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
		if (renderLevel && this.minecraft.level != null) {
			RenderGeneralKt.renderBloom(deltaTracker);
			this.minecraft.getMainRenderTarget().bindWrite(true);
			RenderSystem.clear(256, Minecraft.ON_OSX);
			// TODO: FIX THIS
		}
	}

	@Inject(method = "resize", at = @At("TAIL"))
	private void resize(int width, int height, CallbackInfo ci) {
		if (ModPostChains.INSTANCE.getReady()) {
			ModPostChains.INSTANCE.resize(width, height);
		}
	}
}
