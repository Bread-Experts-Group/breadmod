package org.bread_experts_group.breadmod.mixin.client.rendering;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.GridHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
	@ModifyReturnValue(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At("RETURN"))
	private HitResult pickGrid(
			HitResult original,
			@Local(argsOnly = true) Entity entity,
			@Local(ordinal = 2) double hitDistance
	) {
		PhysicsGrid grid = PhysicsGrid.Companion.getClosestGrid(entity);
		if (grid != null) {
			GridHitResult result = grid.gridBlockCast(entity, hitDistance);
			if (result != null) return result;
		}
		return original;
	}
}
