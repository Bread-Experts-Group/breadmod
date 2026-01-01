package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(Entity.class)
abstract class MixinEntity {
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
		Collection<PhysicsGrid> grids = PhysicsGrid.grids;
		grids.forEach((grid) -> {
			if (entity != null && entity.getBoundingBox().intersects(grid.getBounding().inflate(0.5))) {
				allShapes.addAll(grid.getNearbyShapes(entity));
			}
		});
		cir.setReturnValue(allShapes);
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
	@ModifyExpressionValue(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
			)
	)
	private BlockState spawnSprintParticle(BlockState original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(breadmod$getThis());
		if (grid != null) {
			Vec3 localized = breadmod$getThis().position().subtract(grid.getPos());
			return grid.getMicroLevel().getBlockState(BlockPos.containing(localized.subtract(0.0, 0.1, 0.0)));
		}
		return original;
	}

	@Inject(
			method = "vibrationAndSoundEffectsFromBlock",
			at = @At("HEAD")
	)
	private void probeSoundAttempt(
			BlockPos pos, BlockState state, boolean playStepSound, boolean broadcastGameEvent,
			Vec3 entityPos, CallbackInfoReturnable<Boolean> cir
	) {
		if (breadmod$getThis() instanceof Player) System.out.println(pos + ", " + state + ", " + playStepSound);
	}
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
