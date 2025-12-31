package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(Entity.class)
abstract class MixinEntity {
	@Shadow public abstract Vec3 getEyePosition(float partialTicks);

	@Shadow public abstract Vec3 getViewVector(float partialTicks);

	@Unique
	private Entity breadmod$getThis() {
		return (Entity) (Object) this;
	}

	@Inject(method = "collectColliders", at = @At("TAIL"), cancellable = true)
	private static void collideWithGrids(
			Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox,
			CallbackInfoReturnable<List<VoxelShape>> cir
	) {
		List<VoxelShape> allShapes = new ArrayList<>(cir.getReturnValue());
		Collection<PhysicsGrid> grids = PhysicsGrid.Companion.getGrids();
		grids.forEach((grid) -> {
			if (entity != null && entity.getBoundingBox().intersects(grid.getBounding().inflate(0.5))) {
				allShapes.addAll(grid.getNearbyShapes(entity));
			}
		});
		cir.setReturnValue(allShapes);
	}

	@Inject(method = "pick", at = @At("HEAD"), cancellable = true)
	private void pickGrid(
			double hitDistance, float partialTicks, boolean hitFluids,
			CallbackInfoReturnable<HitResult> cir
	) {
		PhysicsGrid grid = PhysicsGrid.Companion.getClosestGrid(breadmod$getThis());
		if (grid != null) {
			Vec3 vec3 = this.getEyePosition(partialTicks);
			Vec3 vec31 = this.getViewVector(partialTicks);
			Vec3 vec32 = vec3.add(vec31.x * hitDistance, vec31.y * hitDistance, vec31.z * hitDistance);
			BlockHitResult result = grid.getMicroLevel().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, breadmod$getThis()));
			double x = result.getLocation().x;
			double y = result.getLocation().y;
			double z = result.getLocation().z;
			breadmod$getThis().level().addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.0, 0.0);
			cir.setReturnValue(result);
		}
	}

//	@Shadow
//	public boolean noPhysics;
//	@Shadow
//	private EntityDimensions dimensions;
//	@Shadow
//	private Level level;
//	@Unique
//	private @Nullable Vec3 breadmod$lastPlatformPos;
//
//	@Inject(method = "baseTick", at = @At("TAIL"))
//	private void baseTick(CallbackInfo ci) {
//		GridHitResult selected = GeneralKt.blockPhysicsGrid(
//				(grid) -> grid instanceof ClientPhysicsGrid,
//				this.position(),
//				this.position().subtract(0.0, -0.1, 0.0),
//				false,
//				CollisionContext.of(breadmod$getThis())
//		);
//		if (selected != null) {
//			Vec3 gridPosition = selected.getHitResult().getLocation();
//			if (gridPosition != this.breadmod$lastPlatformPos) {
//				if (this.breadmod$lastPlatformPos != null) {
//					Vec3 delta = gridPosition.subtract(this.breadmod$lastPlatformPos).scale(0.5);
//					this.addDeltaMovement(delta);
//				}
//				this.breadmod$lastPlatformPos = gridPosition;
//			}
//		} else this.breadmod$lastPlatformPos = null;
//	}
//
//	@Inject(
//			method = "spawnSprintParticle",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/level/block/state/BlockState;addRunningEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Z",
//					shift = At.Shift.BEFORE
//			)
//	)
//	private void spawnSprintParticle(CallbackInfo ci, @Local LocalRef<BlockState> blockstate) {
//		GridHitResult selected = GeneralKt.blockPhysicsGrid(
//				(grid) -> grid instanceof ClientPhysicsGrid,
//				this.position(),
//				this.position().subtract(0.0, -0.1, 0.0),
//				false,
//				CollisionContext.of(breadmod$getThis())
//		);
//		if (selected != null) blockstate.set(selected.getState());
//	}
//
//	@Shadow
//	public abstract Vec3 getEyePosition();
//	@Shadow
//	public abstract Vec3 position();
//	@Shadow
//	public abstract void addDeltaMovement(Vec3 addend);
//
//	@Shadow
//	public abstract Vec3 getEyePosition(float partialTicks);
//
//	@Shadow
//	public abstract Vec3 getViewVector(float partialTicks);
//
//	@Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
//	private void isInWall(CallbackInfoReturnable<Boolean> cir) {
//		if (this.noPhysics) cir.setReturnValue(false);
//		else {
//			float f = this.dimensions.width() * 0.8F;
//			AABB aabb = AABB.ofSize(this.getEyePosition(), (double) f, 1.0E-6, (double) f);
//			boolean inWorldWall = BlockPos.betweenClosedStream(aabb).anyMatch(
//					pos -> {
//						BlockState blockstate = this.level.getBlockState(pos);
//						return !blockstate.isAir()
//								&& blockstate.isSuffocating(this.level, pos)
//								&& Shapes.joinIsNotEmpty(
//								blockstate.getCollisionShape(this.level, pos)
//										.move((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()),
//								Shapes.create(aabb),
//								BooleanOp.AND
//						);
//					}
//			);
//			if (inWorldWall) cir.setReturnValue(true);
//			else {
//				Vec3 eyePosition = this.getEyePosition();
//				GridHitResult selected = GeneralKt.blockPhysicsGrid(
//						(grid) -> grid instanceof ClientPhysicsGrid,
//						eyePosition,
//						this.position().subtract(0.0, -0.001, 0.0),
//						false,
//						CollisionContext.of(breadmod$getThis())
//				);
//				if (selected != null) {
//					PhysicsGrid grid = selected.getGrid();
//					BlockState blockState = selected.getState();
//					BlockPos eyeBlockPosition = BlockPos.containing(eyePosition);
//					cir.setReturnValue(
//							!blockState.isAir()
//									&& blockState.isSuffocating(grid, eyeBlockPosition)
//									&& Shapes.joinIsNotEmpty(
//									blockState
//											.getCollisionShape(grid, eyeBlockPosition)
//											.move(eyePosition.x, eyePosition.y, eyePosition.z),
//									Shapes.create(aabb),
//									BooleanOp.AND
//							)
//					);
//				} else cir.setReturnValue(false);
//			}
//		}
//	}
}
