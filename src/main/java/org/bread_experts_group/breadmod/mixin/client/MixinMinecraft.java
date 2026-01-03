package org.bread_experts_group.breadmod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
	@ModifyReturnValue(method = "getMainRenderTarget", at = @At(value = "RETURN"))
	private RenderTarget overrideRenderTarget(RenderTarget original) {
		return (CameraTexture.targetBeingRendered != null) ? CameraTexture.targetBeingRendered : original;
	}
}