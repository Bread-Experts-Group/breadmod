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
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGridGlobals;
import org.bread_experts_group.breadmod.util.GeneralKt;
import org.bread_experts_group.breadmod.util.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

	@Inject(method = "collectColliders", at = @At("TAIL"), cancellable = true)
	private static void collectColliders(
			Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox,
			CallbackInfoReturnable<List<VoxelShape>> cir
	) {
		List<VoxelShape> allShapes = new ArrayList<>(cir.getReturnValue());
		for (PhysicsGrid grid : PhysicsGridGlobals.INSTANCE.getGrids().values())
			allShapes.addAll(grid.getWorldVoxelShapes());
		cir.setReturnValue(allShapes);
	}

	@Shadow
	public abstract Vec3 getEyePosition();

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
				HitResult<Pair<PhysicsGrid, BlockState>> result = GeneralKt.rayCast(
						breadmod$getThis(), 1.0, GeneralKt.blocksPhysicsGrids()
				);
				if (result != null) {
					BlockState blockState = result.getHit().component2();
					Vec3 eyePosition = this.getEyePosition();
					BlockPos eyeBlockPosition = BlockPos.containing(eyePosition);
					cir.setReturnValue(
							!blockState.isAir()
									&& blockState.isSuffocating(this.level, eyeBlockPosition)
									&& Shapes.joinIsNotEmpty(
									blockState
											.getCollisionShape(this.level, eyeBlockPosition)
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
