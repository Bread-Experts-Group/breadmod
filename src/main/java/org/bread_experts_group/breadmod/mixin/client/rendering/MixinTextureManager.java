package org.bread_experts_group.breadmod.mixin.client.rendering;

import net.minecraft.client.renderer.texture.TextureManager;
import org.bread_experts_group.breadmod.mixinutil.General;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
abstract class MixinTextureManager {
	@Inject(method = "tick", at = @At("HEAD"))
	private void enableTextureLock(CallbackInfo ci) {
		General.textureLock = true;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void disableTextureLock(CallbackInfo ci) {
		General.textureLock = false;
	}
}
