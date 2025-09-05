package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.bread_experts_group.breadmod.client.render.RenderGeneralKt;
import org.bread_experts_group.breadmod.registry.shader.ModPostChains;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
//	@Shadow
//	@Final
//	private Minecraft minecraft;
//	@Shadow
//	@Nullable
//	private ClientLevel level;
//
//	@Shadow
//	private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
//	}

//	@Unique
//	private LevelRenderer breadmod$getThis() {
//		return (LevelRenderer) (Object) this;
//	}

//	@Inject(
//			method = "renderLevel",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
//			)
//	)
//	private void renderLevel(
//			DeltaTracker deltaTracker,
//			boolean renderBlockOutline,
//			Camera camera,
//			GameRenderer gameRenderer,
//			LightTexture lightTexture,
//			Matrix4f frustumMatrix,
//			Matrix4f projectionMatrix,
//			CallbackInfo ci,
//			@Local PoseStack posestack,
//			@Local MultiBufferSource.BufferSource multibuffersource$buffersource
//	) {
//		HitResult hitresult = this.minecraft.hitResult;
//		if (hitresult instanceof GridBlockHitResult gbHitResult) {
//			if (!ClientHooks.onDrawHighlight(
//					breadmod$getThis(), camera, hitresult, deltaTracker,
//					posestack, multibuffersource$buffersource)
//			) {
//				Objects.requireNonNull(this.level);
//				if (!gbHitResult.getState().isAir() && this.level.getWorldBorder().isWithinBounds(gbHitResult.getLocation())) {
//					VertexConsumer lineConsumer = multibuffersource$buffersource.getBuffer(RenderType.lines());
//					MixinLevelRenderer.renderShape(
//							posestack,
//							lineConsumer,
//							gbHitResult.getState().getShape(
//									gbHitResult.getGrid(),
//									gbHitResult.getLocalBlockPos(),
//									CollisionContext.of(camera.getEntity())
//							),
//							(double) gbHitResult.getLocationGridRelative().x - camera.getPosition().x,
//							(double) gbHitResult.getLocationGridRelative().y - camera.getPosition().y,
//							(double) gbHitResult.getLocationGridRelative().z - camera.getPosition().z,
//							0.0F,
//							0.0F,
//							0.0F,
//							0.4F
//					);
//				}
//			}
//		}
//	}

	@Inject(method = "renderLevel", at = @At(value = "TAIL"))
	private void renderLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		RenderGeneralKt.renderBloom(deltaTracker);
	}

	@Inject(method = "resize", at = @At("TAIL"))
	private void resize(int width, int height, CallbackInfo ci) {
		if (ModPostChains.INSTANCE.getReady()) {
			ModPostChains.INSTANCE.resize(width, height);
		}
	}
}
