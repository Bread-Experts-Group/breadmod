package org.bread_experts_group.breadmod.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import snownee.jade.overlay.RayTracing;

@Mixin(RayTracing.class)
public class MixinRayTracing {
//	@Inject(
//			method = "wrapBlock",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
//			),
//			cancellable = true
//	)
//	private static void wrapBlock(
//			BlockGetter level,
//			BlockHitResult hit,
//			CollisionContext context,
//			CallbackInfoReturnable<BlockState> cir
//	) {
//		if (hit instanceof GridBlockHitResult gHit) cir.setReturnValue(gHit.getState());
//	}
}
