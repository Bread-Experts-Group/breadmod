package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class MixinEntity {
	private static final Logger logger = LogManager.getLogger();

	@Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
	private void isInWall(CallbackInfoReturnable<? super Boolean> cir) {
		// TODO apply fake block logic to this
	}

	@Inject(method = "collide", at = @At("RETURN"), cancellable = true)
	private void collide(Vec3 vec, CallbackInfoReturnable<Vec3> cir) {
		Vec3 konkn = cir.getReturnValue();
		MixinEntity.logger.info(konkn);
		cir.setReturnValue(konkn);
	}
}
