package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadmod.network.serverbound.PhysicsGridRequestPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
abstract class MixinEntity {
	@Inject(method = "collectColliders", at = @At(value = "TAIL"), cancellable = true)
	private static void collectColliders(Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox, CallbackInfoReturnable<List<VoxelShape>> cir) {
		var gridShapes = PhysicsGridRequestPacket.Companion.getGridShapes();
		List<VoxelShape> allShapes = new ArrayList<>(cir.getReturnValue());
		for (List<VoxelShape> shapes : gridShapes) allShapes.addAll(shapes);
		cir.setReturnValue(allShapes);
	}
}
