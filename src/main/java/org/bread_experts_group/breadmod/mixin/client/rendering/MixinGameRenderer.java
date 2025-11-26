package org.bread_experts_group.breadmod.mixin.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture;
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraViewerBlockRenderer;
import org.bread_experts_group.breadmod.experimental.camera_viewer.DummyCamera;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
	@Shadow
	public abstract void resetProjectionMatrix(Matrix4f matrix);

	@Shadow
	public abstract Matrix4f getProjectionMatrix(double fov);

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;dispatchRenderStage(Lnet/neoforged/neoforge/client/event/RenderLevelStageEvent$Stage;Lnet/minecraft/client/renderer/LevelRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;ILnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;)V"))
	private void breadmod$renderCamera(DeltaTracker deltaTracker, CallbackInfo ci) {
		if (CameraViewerBlockRenderer.Companion.getRenderer() == null) return;
		DummyCamera camera = CameraTexture.Companion.getCamera();
		Matrix4f projectionMatrix = this.getProjectionMatrix(70.0);
		PoseStack poseStack = new PoseStack();
		projectionMatrix.mul(poseStack.last().pose());
		this.resetProjectionMatrix(projectionMatrix);
		Quaternionf quaternionf = camera.rotation().conjugate(new Quaternionf());
		Matrix4f frustumMatrix = new Matrix4f().rotation(quaternionf);
	}
}
