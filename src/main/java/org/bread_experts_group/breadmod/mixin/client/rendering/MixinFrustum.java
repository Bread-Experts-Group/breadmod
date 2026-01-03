package org.bread_experts_group.breadmod.mixin.client.rendering;

import net.minecraft.client.renderer.culling.Frustum;
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frustum.class)
public abstract class MixinFrustum {
	@Inject(method = "offsetToFullyIncludeCameraCube", at = @At("HEAD"), cancellable = true)
	private void offsetToFullyIncludeCameraCube(int offset, CallbackInfoReturnable<Frustum> cir) {
		if (CameraTexture.targetBeingRendered != null) {
			cir.setReturnValue((Frustum) (Object) this);
		}
	}
}
