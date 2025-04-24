package org.bread_experts_group.breadmod.mixin.common;

import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadmod.experimental.physics_grid.ClientPhysicsGrid;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGridGlobals;
import org.bread_experts_group.breadmod.util.GeneralKt;
import org.bread_experts_group.breadmod.util.HitResult;
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
		HitResult<Pair<PhysicsGrid, BlockState>> result = GeneralKt.rayCast(
				this.position(), new Vec3(0.0, -0.1, 0.0),
				0.1, GeneralKt.blockPhysicsGridV(this.level)
		);
		if (result != null) {
			Vec3 gridPosition = result.getHit().component1().getPosition();
			if (gridPosition != this.breadmod$lastPlatformPos) {
				if (this.breadmod$lastPlatformPos != null) {
					Vec3 delta = gridPosition.subtract(this.breadmod$lastPlatformPos).scale(0.5);
					this.addDeltaMovement(delta);
				}
				this.breadmod$lastPlatformPos = gridPosition;
			}
		} else this.breadmod$lastPlatformPos = null;
	}

	@Shadow
	public abstract Vec3 getEyePosition();
	@Shadow
	public abstract Vec3 position();
	@Shadow
	public abstract void addDeltaMovement(Vec3 addend);
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
				HitResult<Pair<PhysicsGrid, BlockState>> result = GeneralKt.rayCast(
						eyePosition, new Vec3(0.0, -0.001, 0.0),
						0.001, GeneralKt.blockPhysicsGridV(this.level)
				);
				if (result != null) {
					PhysicsGrid grid = result.getHit().component1();
					BlockState blockState = result.getHit().component2();
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
}
