package org.bread_experts_group.breadmod.mixin.client.compat.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.GridHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.overlay.RayTracing;

@Mixin(RayTracing.class)
public class MixinRayTracing {
	@ModifyExpressionValue(
			method = "wrapBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
			)
	)
	private static BlockState wrapBlock(BlockState original, @Local(argsOnly = true) BlockHitResult hit) {
		return (hit instanceof GridHitResult gridHitResult) ? gridHitResult.getState() : original;
	}
}