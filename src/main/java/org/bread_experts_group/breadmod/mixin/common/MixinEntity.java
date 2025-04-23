package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGridGlobals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
abstract class MixinEntity {
	@Inject(method = "collectColliders", at = @At(value = "TAIL"), cancellable = true)
	private static void collectColliders(
			Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox,
			CallbackInfoReturnable<List<VoxelShape>> cir
	) {
		List<VoxelShape> allShapes = new ArrayList<>(cir.getReturnValue());
		for (PhysicsGrid grid : PhysicsGridGlobals.INSTANCE.getGrids().values())
			allShapes.addAll(grid.getWorldVoxelShapes());
		cir.setReturnValue(allShapes);
	}
}
