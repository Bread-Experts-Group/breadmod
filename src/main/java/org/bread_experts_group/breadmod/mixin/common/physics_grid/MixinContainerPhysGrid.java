package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Container.class)
interface MixinContainerPhysGrid {
	@ModifyReturnValue(
			method = "stillValidBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/player/Player;F)Z",
			at = @At("RETURN")
	)
	private static boolean shouldRedirectStillValid(boolean original, @Local(argsOnly = true) Player player) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return grid != null || original;
	}
}