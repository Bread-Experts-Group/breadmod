package org.bread_experts_group.breadmod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import org.bread_experts_group.breadmod.util.GridBlockHitResult;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Shadow
	@Nullable
	private ClientLevel level;

	@Shadow
	private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
	}

	@Unique
	private LevelRenderer breadmod$getThis() {
		return (LevelRenderer) (Object) this;
	}

	@Inject(
			method = "renderLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
			)
	)
	private void renderLevel(
			DeltaTracker deltaTracker,
			boolean renderBlockOutline,
			Camera camera,
			GameRenderer gameRenderer,
			LightTexture lightTexture,
			Matrix4f frustumMatrix,
			Matrix4f projectionMatrix,
			CallbackInfo ci,
			@Local PoseStack posestack,
			@Local MultiBufferSource.BufferSource multibuffersource$buffersource
	) {
		HitResult hitresult = this.minecraft.hitResult;
		if (hitresult instanceof GridBlockHitResult gbHitResult) {
			if (!ClientHooks.onDrawHighlight(
					breadmod$getThis(), camera, hitresult, deltaTracker,
					posestack, multibuffersource$buffersource)
			) {
				Objects.requireNonNull(this.level);
				if (!gbHitResult.getState().isAir() && this.level.getWorldBorder().isWithinBounds(gbHitResult.getLocation())) {
					VertexConsumer lineConsumer = multibuffersource$buffersource.getBuffer(RenderType.lines());
					MixinLevelRenderer.renderShape(
							posestack,
							lineConsumer,
							gbHitResult.getState().getShape(
									gbHitResult.getGrid(),
									gbHitResult.getLocalBlockPos(),
									CollisionContext.of(camera.getEntity())
							),
							(double) gbHitResult.getLocation().x - camera.getPosition().x,
							(double) gbHitResult.getLocation().y - camera.getPosition().y,
							(double) gbHitResult.getLocation().z - camera.getPosition().z,
							0.0F,
							0.0F,
							0.0F,
							0.4F
					);
				}
			}
		}
	}
}
