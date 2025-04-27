package org.bread_experts_group.breadmod.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadmod.experimental.physics_grid.ClientPhysicsGrid;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGridGlobals;
import org.bread_experts_group.breadmod.util.GeneralKt;
import org.bread_experts_group.breadmod.util.GridBlockHitResult;
import org.bread_experts_group.breadmod.util.GridHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
abstract class MixinEntity {
	@Shadow
	public boolean noPhysics;
	@Shadow
	private EntityDimensions dimensions;
	@Shadow
	private Level level;
	@Unique
	private @Nullable Vec3 breadmod$lastPlatformPos;

	@Inject(method = "collectColliders", at = @At("TAIL"), cancellable = true)
	private static void collectColliders(
			Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox,
			CallbackInfoReturnable<List<VoxelShape>> cir
	) {
		List<VoxelShape> allShapes = new ArrayList<>(cir.getReturnValue());
		for (PhysicsGrid grid : PhysicsGridGlobals.INSTANCE.getGrids().values()) {
			if (grid instanceof ClientPhysicsGrid) allShapes.addAll(grid.getWorldVoxelShapes());
		}
		cir.setReturnValue(allShapes);
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void baseTick(CallbackInfo ci) {
		GridHitResult selected = GeneralKt.blockPhysicsGrid(
				(grid) -> grid instanceof ClientPhysicsGrid,
				this.position(),
				this.position().subtract(0.0, -0.1, 0.0),
				false,
				CollisionContext.of(breadmod$getThis())
		);
		if (selected != null) {
			Vec3 gridPosition = selected.getHitResult().getLocation();
			if (gridPosition != this.breadmod$lastPlatformPos) {
				if (this.breadmod$lastPlatformPos != null) {
					Vec3 delta = gridPosition.subtract(this.breadmod$lastPlatformPos).scale(0.5);
					this.addDeltaMovement(delta);
				}
				this.breadmod$lastPlatformPos = gridPosition;
			}
		} else this.breadmod$lastPlatformPos = null;
	}

	@Inject(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;addRunningEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Z",
					shift = At.Shift.BEFORE
			)
	)
	private void spawnSprintParticle(CallbackInfo ci, @Local LocalRef<BlockState> blockstate) {
		GridHitResult selected = GeneralKt.blockPhysicsGrid(
				(grid) -> grid instanceof ClientPhysicsGrid,
				this.position(),
				this.position().subtract(0.0, -0.1, 0.0),
				false,
				CollisionContext.of(breadmod$getThis())
		);
		if (selected != null) blockstate.set(selected.getState());
	}

	@Shadow
	public abstract Vec3 getEyePosition();
	@Shadow
	public abstract Vec3 position();
	@Shadow
	public abstract void addDeltaMovement(Vec3 addend);

	@Shadow
	public abstract Vec3 getEyePosition(float partialTicks);

	@Shadow
	public abstract Vec3 getViewVector(float partialTicks);

	@Unique
	private Entity breadmod$getThis() {
		return (Entity) (Object) this;
	}

	@Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
	private void isInWall(CallbackInfoReturnable<Boolean> cir) {
		if (this.noPhysics) cir.setReturnValue(false);
		else {
			float f = this.dimensions.width() * 0.8F;
			AABB aabb = AABB.ofSize(this.getEyePosition(), (double) f, 1.0E-6, (double) f);
			boolean inWorldWall = BlockPos.betweenClosedStream(aabb).anyMatch(
					pos -> {
						BlockState blockstate = this.level.getBlockState(pos);
						return !blockstate.isAir()
								&& blockstate.isSuffocating(this.level, pos)
								&& Shapes.joinIsNotEmpty(
								blockstate.getCollisionShape(this.level, pos)
										.move((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()),
								Shapes.create(aabb),
								BooleanOp.AND
						);
					}
			);
			if (inWorldWall) cir.setReturnValue(true);
			else {
				Vec3 eyePosition = this.getEyePosition();
				GridHitResult selected = GeneralKt.blockPhysicsGrid(
						(grid) -> grid instanceof ClientPhysicsGrid,
						eyePosition,
						this.position().subtract(0.0, -0.001, 0.0),
						false,
						CollisionContext.of(breadmod$getThis())
				);
				if (selected != null) {
					PhysicsGrid grid = selected.getGrid();
					BlockState blockState = selected.getState();
					BlockPos eyeBlockPosition = BlockPos.containing(eyePosition);
					cir.setReturnValue(
							!blockState.isAir()
									&& blockState.isSuffocating(grid, eyeBlockPosition)
									&& Shapes.joinIsNotEmpty(
									blockState
											.getCollisionShape(grid, eyeBlockPosition)
											.move(eyePosition.x, eyePosition.y, eyePosition.z),
									Shapes.create(aabb),
									BooleanOp.AND
							)
					);
				} else cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "pick", at = @At("HEAD"), cancellable = true)
	private void pick(
			double hitDistance, float partialTicks,
			boolean hitFluids,
			CallbackInfoReturnable<net.minecraft.world.phys.HitResult> cir
	) {
		Vec3 eyePosition = this.getEyePosition(partialTicks);
		Vec3 viewVector = this.getViewVector(partialTicks);
		Vec3 destination = eyePosition.add(
				viewVector.x * hitDistance,
				viewVector.y * hitDistance,
				viewVector.z * hitDistance
		);
		GridHitResult selected = GeneralKt.blockPhysicsGrid(
				(grid) -> grid instanceof ClientPhysicsGrid,
				eyePosition,
				destination,
				false,
				CollisionContext.of(breadmod$getThis())
		);
		if (selected != null) {
			cir.setReturnValue(
					new GridBlockHitResult(
							selected.getHitResult().getLocation(),
							Direction.getNearest(selected.getHitResult().getLocation()),
							BlockPos.containing(selected.getHitResult().getLocation().add(selected.getGrid().getPosition())),
							selected.getGrid(),
							BlockPos.containing(selected.getHitResult().getLocation()),
							selected.getState()
					)
			);
		}
	}
}
