package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.bread_experts_group.breadmod.util.GridBlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.jade.overlay.RayTracing;

@Mixin(RayTracing.class)
public class MixinRayTracing {
	@Inject(
			method = "wrapBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
			),
			cancellable = true
	)
	private static void wrapBlock(
			BlockGetter level,
			BlockHitResult hit,
			CollisionContext context,
			CallbackInfoReturnable<BlockState> cir
	) {
		if (hit instanceof GridBlockHitResult gHit) cir.setReturnValue(gHit.getState());
	}
}
