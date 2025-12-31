package org.bread_experts_group.breadmod.mixin.client.rendering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import org.bread_experts_group.breadmod.client.render.RenderGeneralKt;
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture;
import org.bread_experts_group.breadmod.experimental.physics_grid.GridHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.bread_experts_group.breadmod.registry.shader.ModPostChains;
import org.bread_experts_group.breadmod.util.GeneralKt;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

	@Unique
	private LevelRenderer breadmod$getThis() {
		return (LevelRenderer) (Object) this;
	}

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

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
	}

	@Inject(
			method = "renderLevel",
	at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;",
			ordinal = 1,
			opcode = Opcodes.GETFIELD
	))
	private void renderGridHitbox(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera,
	                              GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix,
	                              Matrix4f projectionMatrix, CallbackInfo ci,
	                              @Local PoseStack poseStack, @Local MultiBufferSource.BufferSource bufferSource
    ) {
		if (this.minecraft.hitResult instanceof GridHitResult gridHitResult) {
			PhysicsGrid grid = PhysicsGrid.Companion.getClosestGrid(camera.getEntity());
			if (grid != null) {
				if (!ClientHooks.onDrawHighlight(
						breadmod$getThis(), camera, gridHitResult, deltaTracker,
						poseStack, bufferSource)
				) {
					Vec3 resultPos = GeneralKt.toVec3(gridHitResult.getPos());
					Vec3 relativePos = grid.getPos().add(resultPos);
					BlockState state = gridHitResult.getState();
					renderShape(
							poseStack,
							bufferSource.getBuffer(RenderType.lines()),
							state.getShape(grid.getMicroLevel(), gridHitResult.getPos()),
							relativePos.x - camera.getPosition().x,
							relativePos.y - camera.getPosition().y,
							relativePos.z - camera.getPosition().z,
							0.2f,
							0.6f,
							0.2f,
							1.0f
					);
				}
			}
		}
	}

	@Inject(method = "renderLevel", at = @At(value = "TAIL"))
	private void renderLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera,
	                         GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix,
	                         Matrix4f projectionMatrix, CallbackInfo ci
	) {
		RenderGeneralKt.renderBloom(deltaTracker);
	}

	@Inject(method = "resize", at = @At("TAIL"))
	private void resize(int width, int height, CallbackInfo ci) {
		if (ModPostChains.INSTANCE.getReady()) ModPostChains.INSTANCE.resize(width, height);
	}

	@ModifyReturnValue(method = "shouldShowEntityOutlines", at = @At(value = "RETURN"))
	private boolean shouldShowEntityOutlines(boolean original) {
		if (CameraTexture.Companion.getTargetBeingRendered() != null) return false;
		return original;
	}

	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z"), require = 1)
	private boolean renderLevelCameraIsDetatched(boolean original) {
		if (CameraTexture.Companion.getTargetBeingRendered() != null) return true;
		return original;
	}

	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;", ordinal = 3), require = 1)
	private Entity renderLevelCameraGetEntity(Entity original, @Local(ordinal = 0) Entity entity) {
		if (CameraTexture.Companion.getTargetBeingRendered() != null && entity instanceof LocalPlayer) return entity;
		return original;
	}

	@Unique
	public void breadmod$renderLevel() {

	}
}
