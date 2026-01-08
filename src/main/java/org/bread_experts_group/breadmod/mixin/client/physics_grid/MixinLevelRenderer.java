package org.bread_experts_group.breadmod.mixin.client.physics_grid;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import org.bread_experts_group.breadmod.experimental.physics_grid.GridHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
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
abstract class MixinLevelRenderer {
	@Unique
	private LevelRenderer breadmod$getThis() {
		return (LevelRenderer) (Object) this;
	}

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
			PhysicsGrid grid = PhysicsGrid.getClosestGrid(camera.getEntity());
			if (grid != null) {
				if (!ClientHooks.onDrawHighlight(
						breadmod$getThis(), camera, gridHitResult, deltaTracker,
						poseStack, bufferSource)
				) {
					float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
					Vec3 resultPos = GeneralKt.toVec3(gridHitResult.getLocalPos());
					Vec3 relativePos = grid.getPosLerped(partialTick).add(resultPos);
					BlockState state = gridHitResult.getState();
					renderShape(
							poseStack,
							bufferSource.getBuffer(RenderType.lines()),
							state.getShape(grid.getMicroLevel(), gridHitResult.getLocalPos()),
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
}
